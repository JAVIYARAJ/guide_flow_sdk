package com.rajjaviya.guideflow.animation

import android.view.View
import android.view.animation.OvershootInterpolator
import com.rajjaviya.guideflow.R
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

/**
 * Handles entry animations for the tooltip view based on the configured [AnimationType].
 */
internal object StepAnimator {

    fun animateEnter(view: View, type: AnimationType) {
        // Cancel any running view property animations
        view.animate().cancel()
        
        if (type == AnimationType.SPRING_PHYSICS) {
            doSpringAnimation(view, AnimationType.BOUNCE)
        } else {
            doStandardAnimation(view, type)
        }
    }

    private fun doSpringAnimation(view: View, type: AnimationType) {
        // Fade in is handled smoothly by a standard animator
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(250).setInterpolator(null).start()

        val stiffness = SpringForce.STIFFNESS_MEDIUM
        val damping = 0.8f // DAMPING_RATIO_NO_BOUNCY is 1.0f. 0.8f gives a very subtle, premium stop without vibrating.

        when (type) {
            AnimationType.SLIDE_UP -> {
                val finalY = view.translationY
                view.translationY += 100f
                val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, finalY).apply {
                    spring.stiffness = stiffness
                    spring.dampingRatio = damping
                    start()
                }
                view.setTag(R.id.guideflow_spring_y, anim)
            }
            AnimationType.SLIDE_DOWN -> {
                val finalY = view.translationY
                view.translationY -= 100f
                val anim = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, finalY).apply {
                    spring.stiffness = stiffness
                    spring.dampingRatio = damping
                    start()
                }
                view.setTag(R.id.guideflow_spring_y, anim)
            }
            AnimationType.BOUNCE -> {
                // For bounce, we scale it in with a clean, low-bouncy damping ratio
                view.scaleX = 0.5f
                view.scaleY = 0.5f
                
                val animX = SpringAnimation(view, DynamicAnimation.SCALE_X, 1f).apply {
                    spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                    spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY // 0.75f, perfect for UI bounces
                    start()
                }
                val animY = SpringAnimation(view, DynamicAnimation.SCALE_Y, 1f).apply {
                    spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                    spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
                    start()
                }
                view.setTag(R.id.guideflow_spring_x, animX)
                view.setTag(R.id.guideflow_spring_y, animY)
            }
            else -> {}
        }
    }

    private fun doStandardAnimation(view: View, type: AnimationType) {
        when (type) {
            AnimationType.NONE -> {
                view.alpha = 1f
                view.scaleX = 1f
                view.scaleY = 1f
            }
            AnimationType.FADE -> {
                view.alpha = 0f
                view.animate().alpha(1f).setDuration(300).setInterpolator(null).start()
            }
            AnimationType.SLIDE_UP, AnimationType.GLASSMORPHISM -> {
                view.alpha = 0f
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
            else -> {}
        }
    }
}
