package eu.kanade.tachiyomi.ui.reader

import android.content.res.Resources
import android.os.SystemClock
import android.view.MotionEvent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import eu.kanade.tachiyomi.ui.reader.setting.ReaderPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.math.roundToLong
import kotlin.math.roundToInt

private const val MAX_DELAY = 32L
private const val MAX_SWITCH_DELAY = 10_000L
private const val INTERACTION_SKIP_MS = 2_000L
private const val SPEED_FACTOR_DELTA = 0.02f

class ScrollTimer(
    resources: Resources,
    private val listener: ReaderControlDelegate.OnInteractionListener,
    lifecycleOwner: LifecycleOwner,
    private val readerPreferences: ReaderPreferences,
) {

    private val coroutineScope = lifecycleOwner.lifecycleScope
    private var job: Job? = null

    private var delayMs: Long = 16L
    var pageSwitchDelay: Long = 800L
        private set

    private var resumeAt = 0L
    private val isTouchDown = MutableStateFlow(false)
    private val isRunning = MutableStateFlow(false)

    // ✅ Kotatsu-like constant dp-based movement
    private val baseScrollDelta =
        (resources.displayMetrics.density * 5f).toInt().coerceAtLeast(1)

    val isActive: StateFlow<Boolean>
        get() = isRunning

    init {
        // Listen to main speed and multiplier and combine into pixels/sec
        readerPreferences.autoscrollMainSpeed().changes()
            .combine(readerPreferences.autoscrollMultiplier().changes()) { main, mult ->
                main.toFloat() * mult
            }
            .flowOn(Dispatchers.Default)
            .onEach { pixelsPerSecond ->
                onSpeedChanged(pixelsPerSecond)
            }
            .launchIn(coroutineScope)
    }

    fun setActive(value: Boolean) {
        if (isRunning.value != value) {
            isRunning.value = value
            restartJob()
        }
    }

    fun onUserInteraction() {
        resumeAt = SystemClock.elapsedRealtime() + INTERACTION_SKIP_MS
    }

    fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> isTouchDown.value = true
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL,
            -> isTouchDown.value = false
        }
    }

    private var pixelsPerSecond = 0f
    // pixelsPerSecond can be fractional; use a fractional accumulator for smooth scrolling

    private fun onSpeedChanged(pixelsSec: Float) {
        pixelsPerSecond = pixelsSec

        if (pixelsPerSecond <= 0f) {
            delayMs = 0L
            pageSwitchDelay = 0L
            return
        }

        // Use a short tick for smoothness; compute how many pixels to move each tick
        delayMs = 16L
        // per-tick pixel amount will be accumulated as fractional to avoid rounding jitter

        // Keep a reasonable page switch delay
        pageSwitchDelay = 800L

        restartJob()
    }

    private fun restartJob() {
        job?.cancel()
        resumeAt = 0L

        if (!isRunning.value || delayMs == 0L) {
            job = null
            return
        }

        job = coroutineScope.launch(Dispatchers.Default) {
            var accumulator = 0L
            var speedFactor = 1f
            var fractionalAccumulator = 0.0

            while (isActive) {
                // Smooth pause / resume
                if (isPaused()) {
                    speedFactor = (speedFactor - SPEED_FACTOR_DELTA).coerceAtLeast(0f)
                } else if (speedFactor < 1f) {
                    speedFactor = (speedFactor + SPEED_FACTOR_DELTA).coerceAtMost(1f)
                }

                if (speedFactor == 0f) {
                    delayUntilResumed()
                    continue
                }

                delay(delayMs)

                withContext(Dispatchers.Main) {
                    if (!listener.isReaderResumed()) return@withContext

                    // Accumulate fractional pixels for smooth, laminar movement
                    fractionalAccumulator += (pixelsPerSecond * (delayMs / 1000.0)) * speedFactor
                    val delta = fractionalAccumulator.toInt()

                    if (delta > 0) {
                        if (!listener.scrollBy(delta, true)) {
                            accumulator += delayMs
                        }
                        fractionalAccumulator -= delta
                    } else {
                        // no movement happened this tick; count towards page switch
                        accumulator += delayMs
                    }

                    if (accumulator >= pageSwitchDelay) {
                        listener.switchPageBy(1)
                        accumulator -= pageSwitchDelay
                    }
                }

                yield()
            }
        }
    }

    private fun isPaused(): Boolean {
        return isTouchDown.value || resumeAt > SystemClock.elapsedRealtime()
    }

    private suspend fun delayUntilResumed() {
        while (isPaused()) {
            val delayTime = resumeAt - SystemClock.elapsedRealtime()
            if (delayTime > 0) delay(delayTime) else yield()
            isTouchDown.first { !it }
        }
    }
}
