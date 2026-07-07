# Changelog

All notable changes to the GuideFlow SDK will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-07-07

### Added
- **Spring Physics Engine:** Replaced static interpolators with `androidx.dynamicanimation` for true, physics-based iOS-style entry animations (`BOUNCE`, `SLIDE_UP`, `SLIDE_DOWN`).
- **Glassmorphism Blur:** Added hardware-accelerated `RenderEffect` background blur for Android 12+ devices to create stunning, modern overlays.
- **Pulsing Next Button:** Introduced a micro-interaction that subtly pulses the "Next" button after 3 seconds of inactivity to gently guide users.

### Changed
- **Flattened API Architecture:** Simplified configuration by entirely removing `PremiumEffect` from `TourConfig`. Both `SPRING_PHYSICS` and `GLASSMORPHISM` are now integrated directly into the per-step `AnimationType` enum for easier configuration.
- **Glassmorphism Default Animation:** Upgraded the `GLASSMORPHISM` animation type to use a smooth `SLIDE_UP` entry by default.

### Fixed
- **Animation Clipping Bug:** Fixed a bug in `TooltipView` where the Next button's pill-shaped corners were clipped by layout padding during the pulse animation.
- **Garbage Collection Bug:** Fixed an issue where `SpringAnimation` instances were prematurely destroyed by the garbage collector by attaching them to view tags.

## [1.0.1] - 2026-07-07

### Added
- **JSON Parameters Documentation:** Expanded `README.md` with a comprehensive table detailing all supported JSON parameters for remote tour configuration.
- **Enhanced JSON Demo:** Updated `JsonTourDemoActivity` with a rich JSON payload showcasing `spotlightShape`, `animationType`, custom button labels, visibility toggles, and tooltip positioning.

### Fixed
- Fixed an incorrect view ID target (`btnLike_error` to `btnLike`) in the `JsonTourDemoActivity` JSON payload to ensure the demo tour functions correctly.

## [1.0.0] - 2026-07-06

### Added
- **Host-Agnostic Core:** First-class support for `Activity`, `Fragment`, `DialogFragment`, `BottomSheetDialogFragment`, and raw `View` targets via the `GuideFlow.with()` builder.
- **Advanced View Resolution Engine:** 
  - `callbackFlow` based target detection.
  - Automatically waits for views to be laid out before highlighting.
  - Automatically detects detaches and rotation changes, gracefully tearing down the overlay when needed.
- **Smart Auto-Scroller:** Automatically scrolls targets into view if they are hidden inside a `RecyclerView`, `ScrollView`, or `NestedScrollView`.
- **Spotlight Engine:** 
  - Shape configurations: `ROUNDED_RECT` (default), `CIRCLE`, `OVAL`, `RECT`.
  - Customizable corner radii and spotlight padding.
  - Configurable pulsating / breathing idle animations (`TourConfig.spotlightPulseAnimation`).
- **Tooltip UI Engine:**
  - Fully programmatic XML-free floating cards that never conflict with host app themes.
  - Auto-positioning logic (`TooltipPosition.AUTO`) that calculates screen boundaries and forces the tooltip to the side of the spotlight with the most available space.
- **Entrance & Exit Animations:** `StepAnimator` added with support for `FADE`, `SLIDE_UP`, `SLIDE_DOWN`, `BOUNCE`, and `NONE`.
- **Theming & Config:** `TourTheme` and `TourConfig` introduced for granular control over colors, behaviors (like tap-to-dismiss), and borders.
- **Persistence:** Built-in `TourPreferences` to remember if a user has completed a tour. Supply `setTourId("xyz")` to the builder to enable.
- **Accessibility:** TalkBack support natively baked into `TooltipView` via `announceForAccessibility`.

### Changed
- Initial stable release.
