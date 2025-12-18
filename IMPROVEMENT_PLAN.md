# Komikku Reader Improvements - Kotatsu-Inspired Features

## Comprehensive Improvement Plan

### TIER 1: CRITICAL (Reader Experience)
1. ✅ **Autoscroll System** - DONE
   - Speed scale 0.0-1.0 (0.1x-11.0x display)
   - Smooth acceleration/deceleration
   - Touch event tracking
   - Resilient to small gestures

2. **Bottom Action Bar** - IN PROGRESS
   - Save/Screenshot button
   - Bookmark toggle button (move from top)
   - Autoscroll FAB (already done as overlay)
   - Page info/indicator
   - Reading progress

3. **Reading Progress Tracking**
   - Current page / total pages indicator
   - Scroll progress bar for webtoon
   - Chapter progress
   - Session duration

4. **Better Page Position Handling**
   - Save scroll position per page
   - Restore on navigation
   - Handle orientation changes gracefully

### TIER 2: HIGH IMPACT (Quality of Life)
1. **Webtoon Improvements**
   - Webtoon zoom out (collapse margins) - saves vertical space
   - Pull gesture for chapter navigation
   - Smooth scrolling with better acceleration
   - Page indicator overlay

2. **Page Navigation**
   - Page slider at bottom (jump to any page)
   - Double-tap zones visualization
   - Configurable tap areas
   - Keyboard shortcuts

3. **Color/Image Filters**
   - Brightness/contrast controls
   - Invert colors for night mode
   - Grayscale option
   - Custom color filters

4. **Screen Management**
   - Keep screen on toggle
   - Auto screen off timeout
   - Auto rotate with foldable support
   - Immersive fullscreen

5. **Double-Page Mode**
   - Auto-enable on landscape
   - Foldable device support (detect fold line)
   - Sensitivity adjustment slider
   - Left/right page order preference

6. **Zoom Controls**
   - Pinch to zoom gesture
   - Double-tap to zoom toggle
   - Zoom buttons (+ / -)
   - Zoom mode selection (fit, fill, center)
   - Max zoom level adjustment

### TIER 3: POLISH (Advanced Features)
1. **Advanced Tap Grid System**
   - Customizable tap areas
   - Predefined layouts (3x3, 5x5, custom)
   - Assign actions to zones
   - Visual zone preview

2. **Reading Statistics**
   - Chapter read time
   - Pages per session
   - Reading speed calculation
   - Total reading time per manga

3. **Page Save Feature**
   - Screenshot to gallery
   - Add borders/margins
   - Include chapter info
   - Custom watermark

4. **Image Server Selection**
   - Manual server selection for online sources
   - Fallback server handling
   - Quality selection
   - Cache management

5. **Volume Button Control**
   - Enable/disable volume button navigation
   - Volume buttons scroll in webtoon
   - Customizable actions

6. **UI Customization**
   - Dark/light theme per reader
   - Custom accent color
   - Font size options for chapter titles
   - UI element opacity control

### TIER 4: FUTURE ENHANCEMENTS
1. **Performance**
   - Better memory management for large PDFs
   - Image pre-caching optimization
   - Smoother transitions
   - Reduced battery consumption

2. **Accessibility**
   - Screen reader support
   - Text size customization
   - High contrast mode
   - Haptic feedback options

3. **Social Features**
   - Share current page
   - Reading session tracking
   - Discord RPC integration (Kotatsu has this)
   - Sync reading progress with backend

4. **Source Integration**
   - Per-source reader settings
   - Source-specific optimizations
   - Custom user agent handling

## Implementation Priority Timeline

### Week 1 (NOW)
- Bottom action bar (Save + Bookmark buttons)
- Page position persistence
- Better reading progress indicator

### Week 2
- Webtoon improvements (zoom out, pull gesture)
- Page slider/navigator
- Color filter basic options

### Week 3
- Double-page mode with detection
- Zoom controls and gestures
- Screen management (keep screen on)

### Week 4
- Advanced tap grid UI
- Reading statistics
- Volume button control

### Future
- Everything in Tier 3 & 4

## Kotatsu Feature Mapping

| Feature | Kotatsu Location | Komikku Status |
|---------|------------------|---|
| Autoscroll | ScrollTimer + ScrollTimerControlView | ✅ Implemented |
| Save Page | ReaderActivity (FAB) | ❌ To implement |
| Bookmark | ReaderConfigSheet | ❌ To move to bottom bar |
| Page Info | ReaderActivity (toolbar) | ⚠️ Exists, needs enhancement |
| Zoom | ZoomControl widget | ⚠️ Exists, needs enhancement |
| Double Page | ReaderConfigSheet | ⚠️ Exists, needs better UI |
| Rotation | ScreenOrientationHelper | ⚠️ Exists, needs improvement |
| Page Slider | ReaderActionsView | ⚠️ Exists, could be enhanced |
| Color Filter | ReaderSettings | ⚠️ Exists, needs more options |
| Tap Grid | TapGridSettings | ⚠️ Exists, needs UI |
| Discord RPC | DiscordRPCService | ✅ Already in Komikku |
| Chapter Nav | WebtoonReaderFragment | ⚠️ Needs pull gesture |
| Scroll Timer | ScrollTimer | ✅ Implemented (improved) |

