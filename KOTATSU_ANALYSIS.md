# Kotatsu Reader Analysis - Features to Implement

## Key Improvements Found in Kotatsu

### 1. **ScrollTimer (Autoscroll) - CRITICAL IMPROVEMENT**
**File**: `app/src/main/kotlin/org/koitharu/kotatsu/reader/ui/ScrollTimer.kt`

**Current Implementation Issues (Komikku)**:
- Speed stored as seconds (0.5-5.0)
- No touch event tracking
- No acceleration/deceleration
- Simple delay-based scrolling

**Kotatsu Implementation**:
```kotlin
// Speed range: 0.0 to 1.0 (inverse)
// 0.0 = fastest, 1.0 = slowest
private const val MAX_DELAY = 32L
private const val MAX_SWITCH_DELAY = 10_000L
private const val SPEED_FACTOR_DELTA = 0.02f

// Speed factors dynamically adjust
val speedFactor = 1f - speed  // Inverse relationship
delayMs = (MAX_DELAY * speedFactor).roundToLong()
pageSwitchDelay = (MAX_SWITCH_DELAY * speedFactor).roundToLong()

// Touch tracking pauses autoscroll gracefully
fun onTouchEvent(event: MotionEvent) {
    when (event.actionMasked) {
        MotionEvent.ACTION_DOWN -> isTouchDown.value = true
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> isTouchDown.value = false
    }
}

// Smooth resume after touch
private fun isPaused(): Boolean {
    return isTouchDown.value || resumeAt > SystemClock.elapsedRealtime()
}

// Speed factor smoothly accelerates/decelerates (not instant)
if (isPaused()) {
    speedFactor = (speedFactor - SPEED_FACTOR_DELTA).coerceAtLeast(0f)
} else if (speedFactor < 1f) {
    speedFactor = (speedFactor + SPEED_FACTOR_DELTA).coerceAtMost(1f)
}
```

### 2. **ScrollTimerControlView UI** 
**File**: `app/src/main/kotlin/org/koitharu/kotatsu/reader/ui/ScrollTimerControlView.kt`

**Features**:
- Slider range: 0.0 to 1.0 (maps to 0.1x to 10.0x speed display)
- Label formatter shows actual speed multiplier
- Toggle switch to enable/disable
- Real-time speed adjustment
- FAB visibility toggle

**UI Display**:
```kotlin
// Display formula: 0.1 + percent * 10 (ranges from 0.1x to 10.0x)
override fun getFormattedValue(value: Float): String {
    val valueFrom = binding.sliderTimer.valueFrom
    val valueTo = binding.sliderTimer.valueTo
    val percent = (value - valueFrom) / (valueTo - valueFrom)
    return labelPattern.format(0.1 + percent * 10)
}
```

### 3. **Reader Modes**
- Standard (L→R)
- Reversed (R→L)  
- Webtoon (vertical scroll)
- Vertical (vertical pager)
- Double-page modes with foldable support

### 4. **Advanced Features Found**
- **Zoom Controls** - Pinch to zoom, zoom buttons
- **Double-page mode** - With foldable device support
- **Page indicator slider** - Jump to any page
- **Color filter** - Customizable color adjustments
- **Screen rotation** - Lock/auto with configuration
- **Image server selection** - For online sources
- **Page save** - Screenshot with border options
- **Tap grid configuration** - Customizable tap zones

### 5. **Webtoon Specific**
- Webtoon zoom out (collapse margins)
- Pull gesture for chapter navigation
- Smooth scrolling with acceleration
- Page indicator in webtoon mode
- Scaling frame for zoomed content

### 6. **State Management**
- Reading progress tracking
- Scroll position preservation
- Current page state
- Chapter transition handling

## What to Implement in Komikku

### Priority 1 (Critical - Autoscroll)
1. ✅ Replace seconds-based speed with 0.0-1.0 scale
2. ✅ Add touch event tracking
3. ✅ Implement smooth acceleration/deceleration
4. ✅ Better ScrollTimer logic
5. ✅ Improved UI slider with 0.1x to 11.0x display

### Priority 2 (Quality of Life)
1. Webtoon specific improvements
2. Better page position tracking
3. Foldable device support
4. Page save feature
5. Color filter options

### Priority 3 (Polish)
1. Tap grid customization UI
2. Screen rotation options
3. Advanced zoom controls
4. Bookmark integration
5. Reading stats

## Technical Debt
- Use Flow/StateFlow for state management (Kotatsu uses this extensively)
- Dependency injection with Hilt (Kotatsu uses this)
- Modern Kotlin coroutines patterns
- Lifecycle-aware state management

