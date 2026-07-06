package com.rajjaviya.guideflow.overlay

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.spotlight.SpotlightBounds
import com.rajjaviya.guideflow.spotlight.SpotlightShape

/**
 * Full-screen overlay view that renders:
 * 1. A dim background using the color from [TourTheme.overlayColor].
 * 2. A transparent spotlight cutout over the target view using Porter-Duff `CLEAR` mode.
 * 3. An optional border stroke around the spotlight from [TourTheme.spotlightBorderColor].
 *
 * ## How the cutout works
 * The view is rendered on a **software layer**. Drawing the dim first and then
 * clearing a region with `PorterDuff.Mode.CLEAR` makes that region fully transparent,
 * revealing whatever is behind the overlay (the target view).
 *
 * ## Spotlight animation
 * When [updateSpotlight] is called with `animated = true`, the spotlight rect
 * smoothly interpolates from its previous position to the new one via a
 * [ValueAnimator]. This gives a polished step-transition feel.
 */
internal class GuideOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    // -------------------------------------------------------------------------
    // Paints
    // -------------------------------------------------------------------------

    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private val currentBounds = RectF()
    private val targetBounds = RectF()
    private var currentCornerRadius = 0f
    private var targetCornerRadius = 0f
    private var currentShape: SpotlightShape = SpotlightShape.ROUNDED_RECT

    private var spotlightAnimator: ValueAnimator? = null
    private var pulseAnimator: ValueAnimator? = null

    private var currentPulseScale = 1f
    private var isPulseEnabled = true

    /** Called when the user taps outside the spotlight area. */
    var onOutsideTapped: (() -> Unit)? = null

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    init {
        // Required for Porter-Duff CLEAR to work correctly on this view.
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        setWillNotDraw(false)
    }

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    /** Applies [TourTheme] colors to all internal paints. */
    fun applyTheme(theme: TourTheme) {
        dimPaint.color = theme.overlayColor
        borderPaint.color = theme.spotlightBorderColor
        borderPaint.strokeWidth = theme.spotlightBorderWidth
        invalidate()
    }

    /**
     * Updates the spotlight to the given [bounds].
     *
     * @param bounds   New spotlight geometry.
     * @param animated Whether to animate from the previous position. `false` on first step.
     */
    fun updateSpotlight(bounds: SpotlightBounds, animated: Boolean = true, pulseEnabled: Boolean = true) {
        spotlightAnimator?.cancel()
        
        isPulseEnabled = pulseEnabled
        if (isPulseEnabled && pulseAnimator == null) {
            startPulseAnimation()
        } else if (!isPulseEnabled) {
            pulseAnimator?.cancel()
            pulseAnimator = null
            currentPulseScale = 1f
        }

        currentShape = bounds.shape

        if (!animated || currentBounds.isEmpty) {
            currentBounds.set(bounds.bounds)
            currentCornerRadius = bounds.cornerRadius
            targetBounds.set(bounds.bounds)
            targetCornerRadius = bounds.cornerRadius
            invalidate()
            return
        }

        // Snapshot start state
        val startLeft = currentBounds.left
        val startTop = currentBounds.top
        val startRight = currentBounds.right
        val startBottom = currentBounds.bottom
        val startRadius = currentCornerRadius

        targetBounds.set(bounds.bounds)
        targetCornerRadius = bounds.cornerRadius

        spotlightAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = SPOTLIGHT_ANIM_DURATION_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                val fraction = animator.animatedFraction
                currentBounds.set(
                    lerp(startLeft, targetBounds.left, fraction),
                    lerp(startTop, targetBounds.top, fraction),
                    lerp(startRight, targetBounds.right, fraction),
                    lerp(startBottom, targetBounds.bottom, fraction),
                )
                currentCornerRadius = lerp(startRadius, targetCornerRadius, fraction)
                invalidate()
            }
            start()
        }
    }

    /** Clears the spotlight so the full overlay is dimmed (no cutout). */
    fun clearSpotlight() {
        spotlightAnimator?.cancel()
        pulseAnimator?.cancel()
        pulseAnimator = null
        currentPulseScale = 1f
        currentBounds.setEmpty()
        invalidate()
    }

    private fun startPulseAnimation() {
        pulseAnimator = ValueAnimator.ofFloat(1f, 1.04f).apply {
            duration = 1000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = android.view.animation.AccelerateDecelerateInterpolator()
            addUpdateListener { animator ->
                currentPulseScale = animator.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    // -------------------------------------------------------------------------
    // Drawing
    // -------------------------------------------------------------------------

    override fun onDraw(canvas: Canvas) {
        // 1. Dim the entire overlay
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)

        // 2. Punch out the spotlight cutout (only when bounds are set)
        if (!currentBounds.isEmpty) {
            val cx = currentBounds.centerX()
            val cy = currentBounds.centerY()
            
            canvas.save()
            if (currentPulseScale != 1f) {
                canvas.scale(currentPulseScale, currentPulseScale, cx, cy)
            }
            
            drawSpotlightCutout(canvas)

            // 3. Optional border ring around the spotlight
            if (borderPaint.strokeWidth > 0f && borderPaint.color != 0) {
                drawSpotlightBorder(canvas)
            }
            
            canvas.restore()
        }

        super.onDraw(canvas)
    }

    private fun drawSpotlightCutout(canvas: Canvas) {
        when (currentShape) {
            SpotlightShape.CIRCLE, SpotlightShape.OVAL ->
                canvas.drawOval(currentBounds, clearPaint)

            SpotlightShape.ROUNDED_RECT ->
                canvas.drawRoundRect(currentBounds, currentCornerRadius, currentCornerRadius, clearPaint)

            SpotlightShape.RECT ->
                canvas.drawRect(currentBounds, clearPaint)
        }
    }

    private fun drawSpotlightBorder(canvas: Canvas) {
        when (currentShape) {
            SpotlightShape.CIRCLE, SpotlightShape.OVAL ->
                canvas.drawOval(currentBounds, borderPaint)

            SpotlightShape.ROUNDED_RECT ->
                canvas.drawRoundRect(currentBounds, currentCornerRadius, currentCornerRadius, borderPaint)

            SpotlightShape.RECT ->
                canvas.drawRect(currentBounds, borderPaint)
        }
    }

    // -------------------------------------------------------------------------
    // Touch handling
    // -------------------------------------------------------------------------

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            if (!isTouchInsideSpotlight(event.x, event.y)) {
                onOutsideTapped?.invoke()
            }
        }
        // Always consume touches so they don't pass through to views behind the overlay.
        return true
    }

    private fun isTouchInsideSpotlight(x: Float, y: Float): Boolean {
        if (currentBounds.isEmpty) return false
        return currentBounds.contains(x, y)
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun lerp(start: Float, end: Float, fraction: Float): Float =
        start + (end - start) * fraction

    companion object {
        private const val SPOTLIGHT_ANIM_DURATION_MS = 350L
    }
}
