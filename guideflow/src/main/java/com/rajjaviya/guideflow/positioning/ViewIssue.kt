package com.rajjaviya.guideflow.positioning

/**
 * Describes why a target [android.view.View] cannot be resolved into spotlight bounds.
 *
 * Returned by [ViewResolver.detectIssue] so the SDK can log actionable messages
 * and decide how to handle each case.
 */
internal enum class ViewIssue(val message: String) {

    /** The view has not been added to a window yet, or has been removed. */
    NOT_ATTACHED(
        "Target view is not attached to a window. " +
            "Ensure the view is visible before starting the tour step.",
    ),

    /** The view is attached but has not completed its first layout pass. */
    NOT_LAID_OUT(
        "Target view has not been laid out yet. " +
            "The SDK will wait for the next layout pass automatically.",
    ),

    /** The view is laid out but reports zero width or height. */
    ZERO_SIZE(
        "Target view has zero width or height. " +
            "Check that the view has valid layout_width/layout_height constraints.",
    ),

    /** The view is not visible on screen (GONE or INVISIBLE). */
    NOT_VISIBLE(
        "Target view is not visible (GONE or INVISIBLE). " +
            "The spotlight will not be shown for this step.",
    ),
}
