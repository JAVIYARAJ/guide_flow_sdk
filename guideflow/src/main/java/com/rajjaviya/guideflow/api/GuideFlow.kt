package com.rajjaviya.guideflow.api

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rajjaviya.guideflow.controller.TourManager
import com.rajjaviya.guideflow.host.ActivityHost
import com.rajjaviya.guideflow.host.BottomSheetHost
import com.rajjaviya.guideflow.host.DialogHost
import com.rajjaviya.guideflow.host.FragmentHost
import com.rajjaviya.guideflow.host.TourHost
import com.rajjaviya.guideflow.host.ViewHost
import com.rajjaviya.guideflow.listener.TourListener
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourTheme
import com.rajjaviya.guideflow.storage.TourPreferences
import android.content.Context

/**
 * # GuideFlow
 *
 * Primary entry-point for the GuideFlow In-App Tour SDK.
 *
 * Call one of the `with(...)` factory functions matching your host component,
 * then chain the builder methods and call `start()`:
 *
 * ```kotlin
 * // ── Activity ────────────────────────────────────────────────
 * GuideFlow.with(this)                        // 'this' = Activity
 *     .setTourId("onboarding")
 *     .setTheme(TourTheme.dark())
 *     .setConfig(TourConfig.showcase())
 *     .addStep(GuideStep(targetView = fab, title = "Add items", description = "Tap to add"))
 *     .addStep(GuideStep(targetView = menu, title = "Menu", description = "More options here"))
 *     .setListener(myListener)
 *     .start()
 *
 * // ── Fragment ────────────────────────────────────────────────
 * GuideFlow.with(this)                        // 'this' = Fragment
 *     .addStep(...)
 *     .start()
 *
 * // ── DialogFragment ──────────────────────────────────────────
 * GuideFlow.with(myDialog)
 *     .addStep(...)
 *     .start()
 *
 * // ── BottomSheetDialogFragment ────────────────────────────────
 * GuideFlow.with(myBottomSheet)
 *     .addStep(...)
 *     .start()
 *
 * // ── Any View ────────────────────────────────────────────────
 * GuideFlow.with(binding.cardContainer)
 *     .addStep(...)
 *     .start()
 * ```
 */
object GuideFlow {

    /** Current SDK version. */
    const val VERSION = "1.0.0"

    // ------------------------------------------------------------------
    // with() — one overload per supported host type
    // ------------------------------------------------------------------

    /**
     * Creates a [GuideFlowBuilder] scoped to an [Activity].
     * The overlay covers the full screen (Activity DecorView).
     */
    @JvmStatic
    fun with(activity: Activity): GuideFlowBuilder =
        GuideFlowBuilder(ActivityHost(activity))

    /**
     * Creates a [GuideFlowBuilder] scoped to a [Fragment].
     * The overlay is bounded to the fragment's root view.
     * Tour auto-cancels when the fragment view is destroyed.
     */
    @JvmStatic
    fun with(fragment: Fragment): GuideFlowBuilder =
        GuideFlowBuilder(FragmentHost(fragment))

    /**
     * Creates a [GuideFlowBuilder] scoped to a [DialogFragment].
     * The overlay is bounded to the dialog's own window.
     */
    @JvmStatic
    fun with(dialogFragment: DialogFragment): GuideFlowBuilder =
        GuideFlowBuilder(DialogHost(dialogFragment))

    /**
     * Creates a [GuideFlowBuilder] scoped to a [BottomSheetDialogFragment].
     * The overlay is bounded to the bottom sheet's content view.
     */
    @JvmStatic
    fun with(bottomSheet: BottomSheetDialogFragment): GuideFlowBuilder =
        GuideFlowBuilder(BottomSheetHost(bottomSheet))

    /**
     * Creates a [GuideFlowBuilder] scoped to an arbitrary [View].
     *
     * The [LifecycleOwner][androidx.lifecycle.LifecycleOwner] is resolved automatically
     * from the view tree (set by AppCompatActivity / Fragment). If resolution fails,
     * use [with(TourHost)][with] and supply a [ViewHost] with an explicit lifecycle owner.
     *
     * The overlay is attached to [view] if it is a [ViewGroup], or to its first
     * [ViewGroup] ancestor otherwise.
     */
    @JvmStatic
    fun with(view: View): GuideFlowBuilder =
        GuideFlowBuilder(ViewHost.from(view))

    /**
     * Creates a [GuideFlowBuilder] from a fully custom [TourHost].
     * Use this for advanced scenarios not covered by the built-in overloads.
     */
    @JvmStatic
    fun with(host: TourHost): GuideFlowBuilder = GuideFlowBuilder(host)

    /**
     * Checks if a tour with the given [tourId] has already been completed.
     */
    @JvmStatic
    fun isTourCompleted(context: Context, tourId: String): Boolean {
        return TourPreferences(context).isCompleted(tourId)
    }

    /**
     * Clears the completion status of a tour, allowing it to be shown again.
     */
    @JvmStatic
    fun clearTourCompletion(context: Context, tourId: String) {
        TourPreferences(context).clear(tourId)
    }

    /**
     * Clears all stored tour completion data.
     */
    @JvmStatic
    fun clearAllTours(context: Context) {
        TourPreferences(context).clearAll()
    }
}

/**
 * Fluent builder for configuring a GuideFlow tour.
 *
 * Obtain via [GuideFlow.with]. Every method returns `this` for chaining.
 * Call [start] to launch the tour and receive a [TourManager].
 */
class GuideFlowBuilder internal constructor(private val host: TourHost) {

    private val steps = mutableListOf<GuideStep>()
    private var tourId: String? = null
    private var theme: TourTheme = TourTheme.light()
    private var config: TourConfig = TourConfig.default()
    private var listener: TourListener? = null

    // ------------------------------------------------------------------
    // Builder methods
    // ------------------------------------------------------------------

    private var forceShow: Boolean = false

    /**
     * Sets an optional identifier for this tour.
     * Used for analytics and to persist completion state.
     */
    fun setTourId(id: String): GuideFlowBuilder = apply { tourId = id }

    /**
     * Forces the tour to show even if it has already been completed.
     * Useful for debugging or manual replays.
     */
    fun forceShow(force: Boolean = true): GuideFlowBuilder = apply { forceShow = force }

    /**
     * Appends a single [GuideStep] to the tour sequence.
     */
    fun addStep(step: GuideStep): GuideFlowBuilder = apply { steps.add(step) }

    /**
     * Appends multiple [GuideStep]s to the tour sequence.
     */
    fun addSteps(vararg step: GuideStep): GuideFlowBuilder =
        apply { steps.addAll(step) }

    /**
     * Replaces all previously added steps with the provided list.
     */
    fun setSteps(steps: List<GuideStep>): GuideFlowBuilder = apply {
        this.steps.clear()
        this.steps.addAll(steps)
    }

    /**
     * Sets the visual [TourTheme] (colors, spotlight style).
     *
     * Defaults to [TourTheme.light] if not called.
     *
     * ```kotlin
     * .setTheme(TourTheme.dark())
     * .setTheme(TourTheme.materialYou(primaryColor, onPrimaryColor))
     * .setTheme(TourTheme(overlayColor = Color.parseColor("#80000000")))
     * ```
     */
    fun setTheme(theme: TourTheme): GuideFlowBuilder = apply { this.theme = theme }

    /**
     * Sets the runtime behaviour [TourConfig] (dismiss on tap, step indicator, etc.).
     *
     * Defaults to [TourConfig.default] if not called.
     *
     * ```kotlin
     * .setConfig(TourConfig.strict())          // mandatory onboarding
     * .setConfig(TourConfig.showcase())         // free exploration
     * .setConfig(TourConfig(showStepIndicator = true))
     * ```
     */
    fun setConfig(config: TourConfig): GuideFlowBuilder = apply { this.config = config }

    /**
     * Attaches a [TourListener] to receive tour lifecycle callbacks.
     */
    fun setListener(listener: TourListener): GuideFlowBuilder =
        apply { this.listener = listener }

    // ------------------------------------------------------------------
    // Terminal operation
    // ------------------------------------------------------------------

    /**
     * Validates the configuration, starts the tour, and returns a [TourManager]
     * that can be used to control the tour programmatically after launch.
     *
     * If [setTourId] was called and the tour has already been completed,
     * this method returns `null` and the tour does not start. Use [forceShow]
     * to bypass this check.
     *
     * @throws IllegalStateException if no steps have been added.
     */
    fun start(): TourManager? {
        check(steps.isNotEmpty()) {
            "GuideFlow: call addStep() at least once before start()."
        }

        val id = tourId
        if (id != null && !forceShow) {
            val prefs = TourPreferences(host.getContext())
            if (prefs.isCompleted(id)) {
                return null
            }
        }
        return TourManager(
            host = host,
            steps = steps.toList(),
            tourId = tourId,
            theme = theme,
            config = config,
            listener = listener,
        ).also { it.start() }
    }
}
