# ScrollTimer Integration Guide

## What Was Created

1. **ReaderControlDelegate.kt** - Interface for reader interactions
2. **ScrollTimer.kt** - Kotatsu's autoscroll engine (100% port)
3. **AutoscrollControls.kt** - Compose UI components for autoscroll

## What Needs To Be Done (Manual Integration)

### Step 1: Make ReaderActivity implement OnInteractionListener

In `ReaderActivity.kt`, add to class declaration:
```kotlin
class ReaderActivity : 
    Activity(),
    ReaderControlDelegate.OnInteractionListener,  // ADD THIS
    // ... other interfaces
{
```

### Step 2: Implement Required Methods

Add these methods to ReaderActivity:

```kotlin
// Called when user interacts with screen
override fun onUserInteraction() {
    // Optional: show UI, reset timeout, etc
}

// Check if reader is visible
override fun isReaderResumed(): Boolean {
    return lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
}

// Scroll within current page (returns true if scrolled, false if at boundary)
override fun scrollBy(delta: Int, smooth: Boolean): Boolean {
    return (state.value.viewer as? WebtoonViewer)?.let {
        it.scrollBy(delta)  // Adjust based on actual viewer API
    } ?: false
}

// Switch to next/previous page
override fun switchPageBy(delta: Int) {
    if (delta > 0) {
        viewModel.nextPage()
    } else {
        viewModel.prevPage()
    }
}
```

### Step 3: Create ScrollTimer Instance

In ReaderActivity's onCreate or composition:

```kotlin
private val scrollTimer by lazy {
    ScrollTimer(
        resources = resources,
        listener = this,  // ReaderActivity implements listener
        lifecycleOwner = this,
        preferenceStore = PreferenceStore.getInstance(),  // Adapt to your preference system
    )
}
```

### Step 4: Wire Touch Events

In your touch/gesture handling code:
```kotlin
override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (event != null) {
        scrollTimer.onTouchEvent(event)
    }
    return super.onTouchEvent(event)
}
```

### Step 5: Wire Autoscroll Toggle Button

Replace the existing autoscroll FAB with:
```kotlin
// In Compose content
if (scrollTimer.isActive.collectAsState().value) {
    AutoscrollFab(
        isActive = state.autoScroll,
        onToggle = { active ->
            scrollTimer.setActive(active)
        },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
    )
}
```

### Step 6: Remove Old Autoscroll Code

Delete or comment out:
- `enableExhAutoScroll()` function
- `disableExhAutoScroll()` function
- Custom FAB pause/resume button (lines ~843-878)
- AutoScrollHelp dialog (lines ~734-820)
- All `onClickAutoScrollHelp` references
- Top bar autoscroll controls

### Step 7: Fix Speed Storage

The ScrollTimer looks for speed in PreferenceStore with key:
```kotlin
"reader_autoscroll_speed"
```

Ensure your preferences system has:
```kotlin
observeAsObservable(
    READER_AUTOSCROLL_SPEED,  // = "reader_autoscroll_speed"
    0f,  // default speed
)
```

## API Reference

### ScrollTimer Methods

```kotlin
fun setActive(value: Boolean)  // Start/stop autoscroll
fun onUserInteraction()        // Call when user taps
fun onTouchEvent(event: MotionEvent)  // Pass all touch events

val isActive: StateFlow<Boolean>  // Observe active state
```

### OnInteractionListener Methods

```kotlin
fun onUserInteraction()  // Called by ScrollTimer on user input
fun isReaderResumed(): Boolean  // Must return true if reader visible
fun scrollBy(delta: Int, smooth: Boolean): Boolean  // Scroll, return if scrolled
fun switchPageBy(delta: Int)  // Change page (delta: +1 next, -1 prev)
```

## Key Differences from Current Implementation

### Current (Wrong)
- Speed is in seconds per page (0.5 - 5.0 seconds)
- Fixed page-switching logic
- Pauses on ANY menu visibility change
- No smooth acceleration/deceleration

### New (Correct - Kotatsu)
- Speed is normalized 0.0-1.0 (screen height per second)
- Smooth scrolling with accumulator for page switching
- Pauses only on actual touch or user interaction
- Smooth acceleration/deceleration on pause/resume
- Handles all viewer types correctly

## Files Modified

- ReaderActivity.kt (modify class declaration + implement interface + add methods)
- ReaderViewModel.kt (may need preference storage updates)
- Any touch/gesture handlers (wire scrollTimer.onTouchEvent)

## Files Created (Ready to Use)

- ✅ ReaderControlDelegate.kt
- ✅ ScrollTimer.kt
- ✅ AutoscrollControls.kt (Compose components)

## Next: Fix Save/Download Button

The save button in BottomReaderBar should trigger current page save like Kotatsu.

Current implementation:
```kotlin
onSaveImage = { viewModel.saveImage(false) }
```

This should trigger a page save action when tapped in the bottom bar, similar to ReaderPageActionsDialog.

