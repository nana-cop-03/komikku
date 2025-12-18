# Autoscroll Replacement Plan - Kotatsu Implementation

## Current Issues with Komikku Autoscroll

1. **Speed Calculation Wrong**: 0.1x should be ~1% screen height/sec, but it's currently 1 page per 0.1 seconds
2. **FAB Button**: Pause/resume needs to be replaced with Kotatsu's version
3. **Input Fields**: Top bar autoscroll controls need to match Kotatsu
4. **Toggle**: Top bar toggle needs Kotatsu's implementation
5. **Save Button**: Download button doesn't work like Kotatsu's

## Kotatsu ScrollTimer Analysis

### Speed Calculation (Correct Implementation)
```kotlin
// Kotatsu uses normalized 0.0-1.0 speed value
// Where speed represents SCREEN HEIGHT PER SECOND
// Not page count!

private fun onSpeedChanged(speed: Float) {
    if (speed <= 0f) {
        delayMs = 0L  // Stopped
        pageSwitchDelay = 0L
    } else {
        val speedFactor = 1f - speed  // Inverse: higher speed = lower delay
        delayMs = (MAX_DELAY * speedFactor).roundToLong()  // 32ms base
        pageSwitchDelay = (MAX_SWITCH_DELAY * speedFactor).roundToLong()  // 10s base
    }
}

// Speed Range:
// 0.0 = stopped
// 0.5 = moderate scroll (50% of max speed)
// 1.0 = max scroll (minimal delays)
```

### Smooth Acceleration/Deceleration
```kotlin
// Kotatsu smoothly accelerates/decelerates based on touch state
var speedFactor = 1f

while (isActive) {
    if (isPaused()) {  // User is touching screen
        speedFactor = (speedFactor - SPEED_FACTOR_DELTA).coerceAtLeast(0f)
        // Gradually reduce speed (0.02f per iteration)
    } else if (speedFactor < 1f) {
        speedFactor = (speedFactor + SPEED_FACTOR_DELTA).coerceAtMost(1f)
        // Gradually increase back to normal speed
    }
    
    // Apply speed factor to delay
    if (speedFactor == 1f) {
        delay(delayMs)
    } else if (speedFactor == 0f) {
        delayUntilResumed()
        continue
    } else {
        delay((delayMs * (1f + speedFactor * 2)).toLong())
    }
}
```

### Touch & Interaction Handling
```kotlin
// Kotatsu detects when user is interacting
fun onUserInteraction() {
    resumeAt = SystemClock.elapsedRealtime() + INTERACTION_SKIP_MS  // 2s skip
}

fun onTouchEvent(event: MotionEvent) {
    when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> isTouchDown.value = true
        MotionEvent.ACTION_UP,
        MotionEvent.ACTION_CANCEL -> isTouchDown.value = false
    }
}

private fun isPaused(): Boolean {
    return isTouchDown.value || resumeAt > SystemClock.elapsedRealtime()
}
```

### Page Switching Logic
```kotlin
// Accumulates delay to switch pages at precise moments
var accumulator = 0L

if (!listener.scrollBy(scrollDelta, false)) {  // If can't scroll within page
    accumulator += delayMs  // Accumulate time
}

if (accumulator >= pageSwitchDelay) {  // After enough accumulation
    listener.switchPageBy(1)  // Switch to next page
    accumulator -= pageSwitchDelay
}
```

## Required Changes in Komikku

### 1. Remove Custom Autoscroll Code
- Delete: Custom autoscroll implementation in ReaderActivity.kt
- Delete: Custom autoscroll dialog with sliders
- Delete: Custom FAB pause/resume button
- Delete: Custom autoscroll settings in top bar

### 2. Create ScrollTimer.kt (Copy from Kotatsu)
Location: `app/src/main/java/eu/kanade/tachiyomi/ui/reader/ScrollTimer.kt`
- Exactly as Kotatsu's implementation
- No modifications needed (it's architecture-agnostic)
- Dependencies: AppSettings (for speed preference)

### 3. Create ReaderControlDelegate Interface
Location: `app/src/main/java/eu/kanade/tachiyomi/ui/reader/ReaderControlDelegate.kt`
- Define `OnInteractionListener` interface
- Methods: `scrollBy()`, `switchPageBy()`, `isReaderResumed()`

### 4. Implement In ReaderActivity
ReaderActivity must implement `OnInteractionListener` with:
```kotlin
// Scroll within current page
override fun scrollBy(delta: Int, smooth: Boolean): Boolean {
    // Use viewer?.scrollBy() or similar
    // Return false if can't scroll (page boundary)
}

// Switch to next/previous page
override fun switchPageBy(delta: Int) {
    // Call viewer?.page() with current page + delta
}

// Check if reader is in foreground
override fun isReaderResumed(): Boolean {
    // Return true if visible and active
}
```

### 5. Create ScrollTimerControlView
Location: `app/src/main/java/eu/kanade/presentation/reader/ScrollTimerControlView.kt`
- Bottom FAB that toggles autoscroll
- Shows speed indicator
- Single tap: toggle on/off
- Long press: open speed control dialog

### 6. Speed Settings in Preferences
- Store speed in AppSettings/ReaderPreferences
- Range: 0.0-1.0 (normalized)
- Default: 0.0 (off)

## Benefits of Kotatsu Implementation

1. **Correct Speed Calculation**: Actually measures screen height/second
2. **Smooth Acceleration**: Touch-aware speed ramping
3. **Robust Touch Handling**: Detects all types of interaction
4. **Better UX**: FAB is simpler and more intuitive
5. **Smaller Code**: Kotatsu ScrollTimer is ~100 lines vs our ~200+
6. **Battle-Tested**: Used in production by thousands of users

## Implementation Order

1. ✅ Create send-to-telegram.py (Python version)
2. ⏳ Create ScrollTimer.kt from Kotatsu
3. ⏳ Create ReaderControlDelegate interface
4. ⏳ Remove custom autoscroll code from ReaderActivity
5. ⏳ Implement OnInteractionListener in ReaderActivity
6. ⏳ Add ScrollTimerControlView FAB
7. ⏳ Fix save/download button behavior
8. ⏳ Test and validate

## Files to Be Modified/Created

| File | Action | Status |
|------|--------|--------|
| scripts/send-to-telegram.py | CREATE | ✅ DONE |
| .github/workflows/build_push.yml | MODIFY | ✅ DONE |
| .github/workflows/build_release.yml | MODIFY | ✅ DONE |
| app/.../ScrollTimer.kt | CREATE | ⏳ TODO |
| app/.../ReaderControlDelegate.kt | CREATE | ⏳ TODO |
| app/.../ ReaderActivity.kt | MODIFY | ⏳ TODO |
| app/.../ScrollTimerControlView.kt | CREATE | ⏳ TODO |
| app/.../BottomReaderBar.kt | MODIFY | ⏳ TODO |

## Notes

- Kotatsu uses Dagger for DI, we can adapt without it
- Kotatsu uses StateFlow, Komikku likely uses same (Compose/Material3)
- Speed preference storage needs AppSettings integration
- UI can be Compose-based (no need to copy XML layouts)

