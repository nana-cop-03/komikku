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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.math.roundToLong

private const val MAX_DELAY = 32L
private const val MAX_SWITCH_DELAY = 10_000L
private const val INTERACTION_SKIP_MS = 2_000L
private const val SPEED_FACTOR_DELTA = 0.02f

/**
 * Autoscroll timer for reader - based on Kotatsu implementation.
 * Speed is normalized 0.0-1.0 where higher = faster scroll.
 */
class ScrollTimer(
    resources: Resources,
    private val listener: ReaderControlDelegate.OnInteractionListener,
    lifecycleOwner: LifecycleOwner,
    private val readerPreferences: ReaderPreferences,
) {

    private val coroutineScope = lifecycleOwner.lifecycleScope
    private var job: Job? = null
    private var delayMs: Long = 10L
    var pageSwitchDelay: Long = 100L
        private set
    private var resumeAt = 0L
    private var isTouchDown = MutableStateFlow(false)
    private val isRunning = MutableStateFlow(false)
    private val scrollDelta = (resources.displayMetrics.density * 2f).toInt().coerceAtLeast(1)

    val isActive: StateFlow<Boolean>
        get() = isRunning

    init {
        readerPreferences.autoscrollInterval().changes()
            .flowOn(Dispatchers.Default)
            .onEach { speed ->
                onSpeedChanged(speed)
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
            MotionEvent.ACTION_DOWN -> {
                isTouchDown.value = true
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL,
            -> {
                isTouchDown.value = false
            }
        }
    }

    private fun onSpeedChanged(speed: Float) {
        if (speed <= 0f) {
            delayMs = 0L
            pageSwitchDelay = 0L
        } else {
            val eased = speed * speed  // quadratic easing
            delayMs = (MAX_DELAY * (1f - eased)).roundToLong().coerceAtLeast(4L)
            pageSwitchDelay = (MAX_SWITCH_DELAY * (1f - eased)).roundToLong().coerceAtLeast(300L)
        }
        if ((job == null) != (delayMs == 0L)) {
            restartJob()
        }
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
            while (isActive) {
                if (isPaused()) {
                    speedFactor = (speedFactor - SPEED_FACTOR_DELTA).coerceAtLeast(0f)
                } else if (speedFactor < 1f) {
                    speedFactor = (speedFactor + SPEED_FACTOR_DELTA).coerceAtMost(1f)
                }
                if (speedFactor == 1f) {
                    delay(delayMs)
                } else if (speedFactor == 0f) {
                    delayUntilResumed()
                    continue
                } else {
                    delay((delayMs * (1f + speedFactor * 2)).toLong())
                }
                withContext(Dispatchers.Main) {
                    if (!listener.isReaderResumed()) return@withContext

                    if (!listener.scrollBy(scrollDelta, true)) {
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
            if (delayTime > 0) {
                delay(delayTime)
            } else {
                yield()
            }
            isTouchDown.first { !it }
        }
    }
}
