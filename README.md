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
    implementation("io.github.javiyaraj:guideflow:1.0.1")
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

## 🚀 Smart Tour Engine

Instead of just showing steps sequentially, GuideFlow features an intelligent, dynamic tour engine.

### Conditional Steps
Skip steps automatically if a specific condition isn't met. The tour will gracefully proceed to the next valid step in the sequence.
```kotlin
GuideStep(
    targetView = findViewById(R.id.pro_analytics_card),
    title = "Advanced Analytics",
    description = "Available only for Premium users.",
    // The engine evaluates this just before the step is shown.
    // If false, the SDK intelligently skips to the next step!
    condition = { user.isPremium() } 
)
```

### Dynamic Step Insertion & Removal
You can dynamically alter the tour *while it's running* or based on user interactions. Just supply a `tag` to your steps to target them.
```kotlin
val tour = GuideFlow.with(this).start()

// Insert a step on the fly
tour?.addStepAfter("login_step", profileStep)

// Remove steps at runtime
tour?.removeStep("premium_pitch_step")
```

### Automatic Tour Resumption
If the user force-closes the app or gets interrupted, the tour can automatically resume right where they left off.
```kotlin
GuideFlow.with(this)
    .setTourId("onboarding_tour") // Required for saving progress
    .setConfig(TourConfig(resumeWhereLeftOff = true))
    .addStep(...)
    .start()
```

### Fine-Grained Step Targeting & Shapes
Override global configs on a per-step basis. Need the arrow to point exactly at the right edge? Need a specific shape just for one step?
```kotlin
GuideStep(
    targetView = myButton,
    pointerOffset = 0.9f, // Arrow points 90% to the right
    spotlightShape = SpotlightShape.CIRCLE // Overrides global shape config
)
```

## 📜 JSON-Driven Tours

GuideFlow can build entire tours directly from a JSON payload. This is perfect for remote onboarding, A/B testing, or updating marketing tutorials without releasing a new app update.

The SDK maps the `targetId` strings in the JSON to the actual Android view IDs (`R.id.your_view`) dynamically.

```kotlin
val jsonPayload = """
{
  "steps": [
    {
      "targetId": "fab_add",
      "title": "Create Item",
      "description": "Tap here to start.",
      "tooltipPosition": "TOP",
      "animationType": "BOUNCE",
      "spotlightShape": "CIRCLE",
      "pointerOffset": 0.5,
      "showNextButton": true,
      "showSkipButton": true,
      "nextButtonLabel": "Got it",
      "skipButtonLabel": "Dismiss",
      "previousButtonLabel": "Back",
      "tag": "create_item_step"
    },
    {
      "targetId": "profile_image",
      "title": "Your Profile",
      "description": "Manage your settings here.",
      "tooltipPosition": "BOTTOM"
    }
  ]
}
"""

GuideFlow.with(this)
    .loadFromJson(jsonPayload) // Magic happens here ✨
    .start()
```

### Supported JSON Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `targetId` | String | **Required** | The Android View ID name (e.g., `"fab_add"` maps to `R.id.fab_add`). |
| `title` | String | `""` | The title text of the step. |
| `description` | String | `""` | The description text of the step. |
| `tooltipPosition` | String | `"AUTO"` | Position of the tooltip. Options: `AUTO`, `TOP`, `BOTTOM`, `START`, `END`. |
| `animationType` | String | `"FADE"` | Entry animation. Options: `FADE`, `CIRCULAR_REVEAL`, `BOUNCE`, `SLIDE_UP`. |
| `spotlightShape` | String | (Global config) | Overrides step shape. Options: `ROUNDED_RECT`, `CIRCLE`, `OVAL`, `RECT`. |
| `pointerOffset` | Float | `0.5` | Tooltip arrow position ratio from `0.0` (start) to `1.0` (end). |
| `showNextButton` | Boolean| `true` | Controls visibility of the Next/Finish button. |
| `showSkipButton` | Boolean| `true` | Controls visibility of the Skip button. |
| `nextButtonLabel`| String | `"Next"` | Custom text label for the Next button. |
| `skipButtonLabel`| String | `"Skip"` | Custom text label for the Skip button. |
| `previousButtonLabel`| String | `"Back"` | Custom text label for the Previous button. |
| `tag` | String | `null` | A unique string identifier for runtime tour modification. |

> **Pro Tip**: If you are trying to target views that don't have standard XML IDs (like items inside a `RecyclerView` or views built in Compose), you can pass a `fallbackProvider` lambda to `loadFromJson` to resolve the view manually!

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
manager.addStepAfter("tag", step)
manager.removeStep("tag")
```

## License
MIT
