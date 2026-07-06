package com.rajjaviya.guideflow.animation

import android.view.View
import android.view.animation.OvershootInterpolator

/**
 * Handles entry animations for the tooltip view based on the configured [AnimationType].
 */
internal object StepAnimator {

    fun animateEnter(view: View, type: AnimationType) {
        // Cancel any running animations to prevent glitches
        view.animate().cancel()

        when (type) {
            AnimationType.NONE -> {
                view.alpha = 1f
                view.translationY = 0f
                view.scaleX = 1f
                view.scaleY = 1f
            }

            AnimationType.FADE -> {
                view.alpha = 0f
                view.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(null)
                    .start()
            }

            AnimationType.SLIDE_UP -> {
                view.alpha = 0f
                // We add a temporary translation offset to slide it in
                view.translationY += 50f
                view.animate()
                    .alpha(1f)
                    .translationYBy(-50f)
                    .setDuration(300)
                    .setInterpolator(null)
                    .start()
            }

            AnimationType.SLIDE_DOWN -> {
                view.alpha = 0f
                view.translationY -= 50f
                view.animate()
                    .alpha(1f)
                    .translationYBy(50f)
                    .setDuration(300)
                    .setInterpolator(null)
                    .start()
            }

            AnimationType.BOUNCE -> {
                view.alpha = 0f
                view.scaleX = 0.8f
                view.scaleY = 0.8f
                view.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(400)
                    .setInterpolator(OvershootInterpolator(1.5f))
                    .start()
            }
        }
    }
}
