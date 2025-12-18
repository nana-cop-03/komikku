# Complete Reader Feature Improvements - Full List

## TIER 1: CRITICAL IMPROVEMENTS (Reader Core)

### ✅ COMPLETED
- [x] **Autoscroll System (Kotatsu-style)**
  - Speed scale: 0.0-1.0 (displays 0.1x-11.0x)
  - 99-step smooth slider
  - 9 preset buttons
  - Resilient to small gestures
  - FAB pause/resume button
  - Enhanced UI dialog

- [x] **Bottom Action Bar Enhancement**
  - Bookmark button (first position)
  - Save image button (second position)
  - Kotatsu-compatible layout
  - Faster access to key actions

### 🔄 PLANNED (Ready to implement)
- [ ] **Page Progress Indicator**
  - Current page / total pages
  - Visual progress bar
  - Completion percentage

- [ ] **Smart Page Position Tracking**
  - Save scroll position per page
  - Restore on revisit
  - Handle orientation changes
  - Multiple bookmarks support

- [ ] **Reading Progress UI**
  - Session time display
  - Pages per session
  - Chapter completion %
  - Reading speed indicator

---

## TIER 2: HIGH IMPACT FEATURES (User Experience)

### WEBTOON IMPROVEMENTS
- [ ] **Pull Gesture Navigation**
  - Swipe up past top boundary = previous chapter
  - Swipe down past bottom boundary = next chapter
  - Smooth transition animation
  - Visual feedback hint

- [ ] **Webtoon Zoom Out**
  - Collapse margins between pages
  - Save vertical space
  - Customizable margin size
  - Toggle on/off

- [ ] **Better Scroll Animation**
  - Acceleration curve
  - Smooth deceleration
  - Inertial scrolling
  - Adjustable scroll speed

### NAVIGATION ENHANCEMENTS
- [ ] **Page Slider/Navigator**
  - Interactive slider at bottom
  - Jump to any page
  - Visual page preview on hover
  - Keyboard support (page up/down)

- [ ] **Double-Tap Navigation**
  - Toggle double-page mode
  - Quick zoom to fit
  - Configurable actions per zone

### VISUAL CONTROLS
- [ ] **Color Filter System**
  - Brightness slider (-100 to +100)
  - Contrast adjustment (-50 to +50)
  - Invert colors toggle
  - Sepia tone option
  - Grayscale mode
  - Save filter profiles

- [ ] **Image Enhancement**
  - Sharpness control
  - Saturation adjustment
  - Hue rotation
  - Saturation slider

### SCREEN MANAGEMENT
- [ ] **Keep Screen On**
  - Toggle button
  - Auto-disable on exit
  - Timeout options (5/10/30 min)
  - Custom timeout input

- [ ] **Auto Rotation**
  - Detect device orientation
  - Lock to portrait/landscape
  - Sensor-based rotation
  - Foldable device detection

---

## TIER 3: ADVANCED FEATURES (Power User)

### DOUBLE-PAGE MODE
- [ ] **Landscape Auto-Enable**
  - Detect landscape orientation
  - Auto-switch to double-page
  - Adjustable sensitivity (slider)
  - Left/right page order option

- [ ] **Foldable Device Support**
  - Detect fold line position
  - Align pages to fold
  - Auto-enable double-page
  - Per-device settings

- [ ] **Page Layout Options**
  - Single page (default)
  - Double pages (dual spread)
  - Cover spread (first page alone)
  - Custom margin control

### ZOOM & SCALING
- [ ] **Pinch to Zoom**
  - Two-finger pinch gestures
  - Smooth zoom animation
  - Min/max zoom constraints
  - Double-tap to reset

- [ ] **Zoom Buttons**
  - + button (zoom in)
  - - button (zoom out)
  - Fit button (auto-fit)
  - Visibility toggle

- [ ] **Zoom Modes**
  - Fit to screen (default)
  - Fit width
  - Fit height
  - Fill screen
  - Actual size (100%)
  - Custom zoom level

### TAP GRID SYSTEM
- [ ] **Customizable Tap Zones**
  - 3x3 grid (9 zones)
  - 5x5 grid (25 zones)
  - Custom grid size
  - Visual zone editor

- [ ] **Zone Actions**
  - Next/previous page
  - Next/previous chapter
  - Toggle menu
  - Open settings
  - Save page
  - Bookmark chapter
  - Custom actions

- [ ] **Tap Grid UI**
  - Zone preview overlay
  - Visual grid editor
  - Drag-and-drop action assignment
  - Preset templates

### KEYBOARD SUPPORT
- [ ] **Keyboard Navigation**
  - Arrow keys (left/right/up/down)
  - Page up/page down
  - Home/end keys
  - Space bar (next page)
  - Back key (exit reader)
  - Custom key bindings

- [ ] **Volume Button Control**
  - Enable/disable in settings
  - Volume up = next page
  - Volume down = previous page
  - In webtoon: volume buttons scroll

---

## TIER 4: POLISH & ANALYTICS

### READING STATISTICS
- [ ] **Per-Session Tracking**
  - Session start/end time
  - Pages read in session
  - Reading duration
  - Average pages per minute

- [ ] **Per-Manga Statistics**
  - Total read time
  - Total pages read
  - Average reading speed
  - Last read date/time

- [ ] **Statistics Dashboard**
  - Time spent today
  - Average session length
  - Most read manga
  - Reading streaks

### PAGE SAVE FEATURE
- [ ] **Save Dialog Options**
  - Image quality slider
  - Add borders toggle
  - Border color picker
  - Include chapter info
  - Custom watermark
  - Resolution selection

- [ ] **Save Destinations**
  - Gallery folder
  - Custom folder
  - Archive creation
  - Cloud upload (future)

- [ ] **Batch Operations**
  - Save current page
  - Save chapter (all pages)
  - Save range (pages X-Y)

### BOOKMARK MANAGEMENT
- [ ] **Bookmark Features**
  - Mark current page as bookmark
  - Named bookmarks
  - Quick bookmark list
  - Jump to bookmark
  - Delete bookmarks

- [ ] **Bookmark Sync**
  - Cloud backup
  - Sync across devices
  - Export/import bookmarks

---

## TIER 5: SOCIAL & INTEGRATION

### SHARING FEATURES
- [ ] **Social Sharing**
  - Share current page
  - Share chapter link
  - Add personal notes
  - Include reading time

- [ ] **Reading Progress Sharing**
  - Share "Currently reading"
  - Completed chapters list
  - Total reading time
  - Favorite genres

### DISCORD INTEGRATION
- [ ] **Discord Rich Presence**
  - Show manga title
  - Current chapter
  - Reading time
  - Cover art
  - Status: Reading/Paused

### EXTERNAL INTEGRATIONS
- [ ] **Source-Specific Features**
  - Image server selection
  - Quality preferences
  - Custom headers
  - User-agent spoofing

---

## IMPLEMENTATION PRIORITY MATRIX

### Quick Wins (< 30 min each)
1. Page progress indicator
2. Keep screen on toggle
3. Zoom mode selection
4. Keyboard arrow key navigation

### Medium Effort (30-60 min each)
1. Webtoon pull gesture
2. Color filter system
3. Auto-rotation detection
4. Page save dialog

### Complex Features (1-3 hours each)
1. Tap grid customization UI
2. Reading statistics system
3. Double-page foldable detection
4. Zoom pinch gesture with smooth animation

### Polish & Refinement (ongoing)
1. Performance optimization
2. Memory management for large PDFs
3. Animation smoothness
4. Edge case handling

---

## Estimated Development Time

- **Quick Wins**: ~2-3 hours total
- **Medium Features**: ~4-6 hours total
- **Complex Features**: ~8-12 hours total
- **Polish & Testing**: ~3-5 hours total

**Total Estimated**: 17-26 hours of development

---

## Feature Comparison Table

| Feature | Kotatsu | Komikku | Priority |
|---------|---------|---------|----------|
| Autoscroll Speed | 0.1-10x | ✅ 0.1-11x | ✅ Done |
| Bottom Bar | Yes | ✅ Yes | ✅ Done |
| Page Indicator | Yes | ❌ No | HIGH |
| Webtoon Pull | Yes | ❌ No | HIGH |
| Color Filters | Yes | ⚠️ Limited | HIGH |
| Pinch Zoom | Yes | ✅ Yes | MED |
| Double-Page | Yes | ✅ Yes | MED |
| Tap Grid UI | Yes | ❌ No | LOW |
| Statistics | Basic | ❌ No | LOW |
| Volume Keys | Yes | ⚠️ Partial | MED |
| Screen On | Yes | ❌ No | HIGH |

---

## Next Immediate Actions (Next Session)

1. Implement page progress indicator
2. Add keep-screen-on toggle
3. Create webtoon pull gesture detector
4. Build color filter UI controls
5. Add auto-rotation detection

