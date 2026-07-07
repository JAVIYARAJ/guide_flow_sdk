package com.rajjaviya.guideflow.model

import android.view.View
import com.rajjaviya.guideflow.animation.AnimationType
import com.rajjaviya.guideflow.tooltip.TooltipPosition
import com.rajjaviya.guideflow.spotlight.SpotlightShape

/**
 * Represents a single step in a GuideFlow tour.
 *
 * @property targetView       The view to highlight. Must be attached to a window when shown.
 * @property title            Short headline shown in the tooltip.
 * @property description      Body text shown below the title.
 * @property tooltipPosition  Preferred position of the tooltip. SDK auto-flips if needed.
 * @property animationType    Entry/exit animation for this step.
 * @property showNextButton   Whether to show a "Next" / "Finish" button.
 * @property nextButtonLabel  Custom label for the next button.
 * @property showSkipButton   Whether to show a "Skip" button.
 * @property skipButtonLabel  Custom label for the skip button.
 * @property previousButtonLabel Custom label for the previous/back button.
 * @property pointerOffset    Offset (0.0 to 1.0) of where the arrow points on the spotlight. Default 0.5 (center).
 * @property spotlightShape   Optional shape for this step's spotlight. Overrides the global TourConfig shape if provided.
 * @property tag              Optional identifier for analytics or testing.
 * @property customContentLayoutRes Optional layout resource to inflate inside the tooltip instead of title/description.
 * @property customContentView Optional custom view instance to inject inside the tooltip instead of title/description.
 */
data class GuideStep(
    val targetView: View,
    val title: CharSequence = "",
    val description: CharSequence = "",
    val tooltipPosition: TooltipPosition = TooltipPosition.AUTO,
    val animationType: AnimationType = AnimationType.FADE,
    val showNextButton: Boolean = true,
    val nextButtonLabel: String = "Next",
    val showSkipButton: Boolean = true,
    val skipButtonLabel: String = "Skip",
    val previousButtonLabel: String = "Back",
    val pointerOffset: Float = 0.5f,
    val spotlightShape: SpotlightShape? = null,
    val condition: () -> Boolean = { true },
    val tag: String? = null,
    @androidx.annotation.LayoutRes val customContentLayoutRes: Int? = null,
    val customContentView: View? = null,
)
