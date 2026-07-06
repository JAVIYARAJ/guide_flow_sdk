# GuideFlow

**GuideFlow** is a modern, lightweight, and incredibly flexible in-app tour and onboarding SDK for Android. It helps you guide users through your app's features with beautiful spotlights, customizable tooltips, and smooth animations.

> Fully built in Kotlin, 100% host-agnostic, and completely XML-free under the hood.

## Features

- 🎯 **Host-Agnostic**: Works seamlessly with `Activity`, `Fragment`, `DialogFragment`, `BottomSheetDialogFragment`, or even a standalone `View`.
- 🧩 **Advanced UI Support**: Automatically scrolls to targets inside `RecyclerView`, `ScrollView`, `NestedScrollView`, and `ViewPager2`.
- 🎨 **Fully Themed**: Light, Dark, and Material You support out of the box. Every color is customizable.
- ✨ **Spotlight Cutouts**: Supports `ROUNDED_RECT`, `CIRCLE`, `OVAL`, and sharp `RECT` shapes with idle breathing animations.
- ♿ **Accessibility First**: Automatically announces step titles and descriptions to TalkBack when the tooltip appears.
- 💾 **Built-in Persistence**: Remembers which tours a user has already completed so you don't annoy them twice.

## Installation

*(Coming soon to Maven Central)*

```kotlin
dependencies {
    implementation("com.rajjaviya:guideflow:1.0.0")
}
```

## Quick Start

```kotlin
val fab = findViewById<FloatingActionButton>(R.id.fab)
val menu = findViewById<View>(R.id.action_menu)

GuideFlow.with(this) // 'this' can be an Activity or Fragment
    .setTourId("home_onboarding") // Used for persistence
    .setTheme(TourTheme.dark())
    .setConfig(TourConfig(spotlightPulseAnimation = true))
    .addStep(
        GuideStep(
            targetView = fab,
            title = "Create New Item",
            description = "Tap here to add a new item to your list.",
            animationType = AnimationType.BOUNCE
        )
    )
    .addStep(
        GuideStep(
            targetView = menu,
            title = "Settings",
            description = "Customize your experience here.",
            tooltipPosition = TooltipPosition.BOTTOM
        )
    )
    .start()
```

## Advanced Configuration

You can customize almost everything about how the tour looks and behaves.

### Themes (`TourTheme`)
```kotlin
val theme = TourTheme(
    overlayColor = Color.parseColor("#CC000000"),
    tooltipBackgroundColor = Color.WHITE,
    nextButtonColor = Color.BLUE,
    spotlightBorderColor = Color.YELLOW,
    spotlightBorderWidth = 4f
)
```

### Behaviors (`TourConfig`)
```kotlin
val config = TourConfig(
    dismissOnOverlayClick = false, // Force the user to interact with the buttons
    enablePreviousButton = true,   // Allow navigating backwards
    scrollToTarget = true,         // Auto-scroll RecyclerViews
    spotlightShape = SpotlightShape.CIRCLE,
    spotlightPadding = 24          // dp
)
```

## Manual Control
Calling `start()` returns a `TourManager`. You can use this to control the tour programmatically from your own UI if you disable the built-in tooltip buttons.

```kotlin
val manager = GuideFlow.with(this).addStep(...).start()

manager.next()
manager.previous()
manager.pause()
manager.resume()
manager.skip()
```

## License
MIT
