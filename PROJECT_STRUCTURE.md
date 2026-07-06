# GuideFlow SDK — Project Structure

A reference guide for every folder and file in this repository.
Any developer joining the project should read this first.

---

## Repository Layout

```
GuideFlow/
│
├── .github/
│   └── workflows/
│       └── ci.yml                  ← GitHub Actions CI pipeline
│
├── app/                            ← Sample application (not shipped)
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/rajjaviya/guideflow/sample/
│           └── (sample screens added per milestone)
│
├── guideflow/                      ← SDK library module (published artifact)
│   └── src/main/java/com/rajjaviya/guideflow/
│       ├── api/
│       ├── controller/
│       ├── host/
│       ├── model/
│       ├── overlay/
│       ├── spotlight/
│       ├── tooltip/
│       ├── positioning/
│       ├── animation/
│       ├── storage/
│       ├── listener/
│       ├── util/
│       └── internal/
│
├── config/
│   └── detekt/
│       └── detekt.yml              ← Detekt static analysis rules
│
├── gradle/
│   ├── libs.versions.toml          ← Version catalog (single source of truth for deps)
│   └── wrapper/                    ← Gradle wrapper binaries
│
├── build.gradle.kts                ← Root build file (plugin declarations only)
├── settings.gradle.kts             ← Multi-module settings (:app + :guideflow)
├── gradle.properties               ← JVM args, parallel builds, config cache
└── .gitignore
```

---

## Root Files

| File | Purpose |
|------|---------|
| `build.gradle.kts` | Declares all Gradle plugins with `apply false`. Modules opt-in individually. |
| `settings.gradle.kts` | Registers modules (`:app`, `:guideflow`) and configures repositories. |
| `gradle.properties` | Global build settings — JVM heap (4 GB), parallel execution, configuration cache, Kotlin code style. |
| `.gitignore` | Excludes IDE files, build outputs, and lint reports from Git. |

---

## `.github/workflows/`

| File | Purpose |
|------|---------|
| `ci.yml` | GitHub Actions pipeline. Runs on every push/PR to `main` and `develop`. Steps: **Detekt → KtLint → assembleDebug → unit tests**. |

---

## `config/detekt/`

| File | Purpose |
|------|---------|
| `detekt.yml` | Detekt rule configuration. Extends the default ruleset. SDK-friendly overrides (e.g. `MagicNumber` disabled, `UndocumentedPublicClass` relaxed for internal packages). |

---

## `gradle/`

| File | Purpose |
|------|---------|
| `libs.versions.toml` | **Version catalog** — the single source of truth for all dependency versions and plugin IDs. All modules reference this file via `libs.*` accessors. |
| `gradle-daemon-jvm.properties` | Auto-generated. Specifies the JDK version (21) used by the Gradle daemon via Foojay toolchain resolver. Do not edit manually. |
| `wrapper/` | Gradle wrapper JAR and properties. Gradle version: **9.4.1**. |

---

## `app/` — Sample Application

The sample app exists only to demonstrate and manually test the SDK.
**It is never published.**

| Path | Purpose |
|------|---------|
| `build.gradle.kts` | App module config — `compileSdk 36`, `minSdk 23`, ViewBinding enabled, depends on `:guideflow`. |
| `src/main/AndroidManifest.xml` | Application manifest for the sample app. |
| `src/main/res/` | Sample app resources (theme, launcher icons, strings). |

---

## `guideflow/` — SDK Library Module

This is the **published artifact**. Everything here ends up in the consumer's app.

### `guideflow/build.gradle.kts`

| Config | Value |
|--------|-------|
| Plugin | `com.android.library` |
| `namespace` | `com.rajjaviya.guideflow` |
| `compileSdk` | 36 |
| `minSdk` | 23 |
| ViewBinding | Enabled |
| Static analysis | Detekt + KtLint |
| Publishing | Vanniktech Maven Publish — `com.rajjaviya:guideflow:0.1.0-SNAPSHOT` |

---

## `guideflow/src/main/java/com/rajjaviya/guideflow/`

### Package Reference

```
com.rajjaviya.guideflow/
│
├── api/            ← Public-facing surface of the SDK          [M2 ✅]
├── controller/     ← Tour lifecycle and step orchestration      [M2 ✅]
├── host/           ← Host adapters (Activity, Fragment, etc.)   [M2 ✅]
├── model/          ← Data classes (GuideStep, TourState, etc.)  [M2 ✅]
├── listener/       ← Callback interfaces (TourListener)         [M2 ✅]
├── overlay/        ← Full-screen dim overlay view               [M3]
├── spotlight/      ← Spotlight cutout rendering                 [M3]
├── tooltip/        ← Tooltip view and arrow rendering           [M4]
├── positioning/    ← Coordinate math (scroll, window offsets)  [M4]
├── animation/      ← Step entry/exit animations                [M4]
├── storage/        ← Tour completion persistence               [M6]
├── util/           ← Extension functions, view utilities       [M4]
└── internal/       ← Private implementation details           [ongoing]
```

---

### Package Details

#### `api/`
**Public SDK entry-point.** Everything a consumer imports comes from here.
- `GuideFlow` — Singleton entry-point (`GuideFlow.with(activity).addStep(...).start()`)
- `GuideFlowBuilder` — Fluent builder for configuring and launching a tour

#### `controller/`
**Tour brain.** Manages step sequencing, state transitions, and listener dispatch.
- `TourController` — `internal` class. Owns a `StateFlow<TourState>`. Drives next/previous/skip/finish.

#### `host/`
**Host adapters.** Each host type (Activity, Fragment, etc.) has its own adapter
that knows how to attach the overlay and survive configuration changes.
- `ActivityHost` — attaches to `DecorView`
- `FragmentHost` — attaches to fragment's root view
- `DialogFragmentHost` — handles dialog window
- `BottomSheetHost` — handles BottomSheetDialogFragment

#### `model/`
**Data layer.** Pure Kotlin data classes, no Android framework dependencies.
- `GuideStep` — A single tour step (target view, title, description, animation, position)
- `TourState` — Immutable snapshot of tour runtime state (currentIndex, isRunning, steps)
- `TourConfig` — Global SDK configuration (overlay color, default animation, skip behavior)

#### `overlay/`
**Dim overlay.** Renders the transparent-background-with-spotlight effect.
- `GuideOverlayView` — Custom `FrameLayout`. Uses Porter-Duff `CLEAR` mode to punch a spotlight hole.
- `OverlayManager` — Attaches / detaches `GuideOverlayView` on the host window's `DecorView`.

#### `spotlight/`
**Spotlight geometry.** Calculates where the cutout should be drawn.
- `SpotlightCalculator` — Converts a `View`'s position to window coordinates (`getLocationInWindow`).

#### `tooltip/`
**Tooltip UI.** Renders the floating card that shows step title, description, and buttons.
- `TooltipView` — Inflates `tooltip_view.xml`, binds data, renders arrow.
- `TooltipPosition` — Enum: `AUTO`, `TOP`, `BOTTOM`, `START`, `END`.
- `TooltipRenderer` — Positions and adds `TooltipView` to the overlay container.

#### `positioning/`
**Coordinate math.** Handles edge cases like scroll offsets, RTL layouts, and
views inside `RecyclerView` / `ViewPager2`.
- `PositionCalculator` — Resolves final screen rect for any view type.

#### `animation/`
**Step transitions.** All enter/exit animations are centralized here.
- `AnimationType` — Enum: `FADE`, `SLIDE_UP`, `SLIDE_DOWN`, `BOUNCE`, `NONE`.
- `StepAnimator` — Applies `ViewPropertyAnimator` based on `AnimationType`.

#### `storage/`
**Persistence.** Remembers which tours the user has already seen.
- `TourPreferences` — Wraps `SharedPreferences`. Stores completed tour IDs.

#### `listener/`
**Callbacks.** Interfaces consumed by the host app to react to tour events.
- `GuideFlowListener` — `onTourStarted`, `onStepChanged`, `onStepCompleted`, `onTourSkipped`, `onTourCompleted`.

#### `util/`
**Helpers.** Zero-dependency utilities used across the SDK.
- Extension functions: `View.isVisible()`, `Int.dp()`, `Context.color()`
- `ViewUtils` — Checks view attachment, measures available space.

#### `internal/`
**Private implementation.** Classes here are annotated `@InternalGuideFlowApi`
or marked `internal` and must **never** be referenced by consumer code.

---

## Dependency Overview

```
libs.versions.toml
│
├── AGP 9.2.1              ← Android Gradle Plugin
├── Kotlin 2.1.21          ← Kotlin stdlib / coroutines version ref
│
├── AndroidX
│   ├── core-ktx 1.16.0
│   ├── appcompat 1.7.1
│   ├── activity-ktx 1.10.1
│   ├── fragment-ktx 1.8.8
│   ├── recyclerview 1.4.0
│   ├── viewpager2 1.1.0
│   ├── constraintlayout 2.2.1
│   ├── coordinatorlayout 1.3.0
│   └── lifecycle-runtime-ktx 2.9.1
│
├── Material Components 1.12.0
├── Kotlinx Coroutines 1.10.2
│
├── Navigation (prepared, added Milestone 2+)
│   ├── navigation-fragment-ktx 2.9.0
│   └── navigation-ui-ktx 2.9.0
│
├── Testing
│   ├── JUnit 4.13.2
│   ├── MockK 1.14.2
│   ├── kotlinx-coroutines-test 1.10.2
│   ├── androidx.test.ext:junit 1.2.1
│   └── espresso-core 3.6.1
│
└── Static Analysis / Publishing
    ├── Detekt 1.23.8
    ├── KtLint Gradle Plugin 12.2.0
    └── Vanniktech Maven Publish 0.29.0
```

---

## CI Pipeline (`.github/workflows/ci.yml`)

```
Push / PR to main or develop
        │
        ▼
   Checkout code
        │
        ▼
   Setup JDK 17 (Temurin)
        │
        ▼
   ./gradlew detekt          ← Static analysis
        │
        ▼
   ./gradlew ktlintCheck     ← Code style
        │
        ▼
   ./gradlew assembleDebug   ← Build verification
        │
        ▼
   ./gradlew test            ← Unit tests
```

---

## Milestones Map

| Milestone | Status | Packages / Files |
|-----------|--------|------------------|
| **v0.1 — Foundation** | ✅ Done | Project setup, all packages created |
| **v0.2 — SDK Architecture** | ✅ Done | `host/*`, `model/GuideStep+Session+State`, `controller/TourController+Manager`, `listener/TourListener` |
| **v0.3 — Public API** | ✅ Done | `api/GuideFlow`, `model/TourTheme`, `model/TourConfig` |
| **v0.4 — Overlay Engine** | ✅ Done | `overlay/GuideOverlayView`, `overlay/OverlayManager` |
| **v0.5 — View Resolution Engine** | ✅ Done | `positioning/ViewResolver+ViewLayoutObserver+ViewIssue`, `util/ViewExtensions` |
| **v0.6 — Spotlight Engine** | ✅ Done | `spotlight/SpotlightCalculator+Bounds+Shape` |
| **v0.7 — Tooltip Engine** | ✅ Done | `tooltip/TooltipView+TooltipRenderer+TooltipPosition` |
| **v0.8 — Host Adapters (advanced)** | ✅ Done | `positioning/AutoScroller` — ScrollView, RecyclerView, CoordinatorLayout |
| **v0.9 — Persistence** | ✅ Done | `storage/TourPreferences` |
| **v1.0 — Themes & Accessibility** | ✅ Done | Animations, TalkBack, Documentation (README, CHANGELOG) |
| **v1.1 — Maven Release** | 🔜 Next | Publishing, ProGuard |
