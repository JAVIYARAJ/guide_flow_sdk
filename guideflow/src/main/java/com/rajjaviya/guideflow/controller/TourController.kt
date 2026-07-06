package com.rajjaviya.guideflow.controller

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.rajjaviya.guideflow.listener.TourListener
import com.rajjaviya.guideflow.model.TourSession
import com.rajjaviya.guideflow.model.TourState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.rajjaviya.guideflow.storage.TourPreferences
import android.content.Context

/**
 * Controls step navigation and owns the [TourState] for a single tour run.
 *
 * This class is **internal** — consumers never interact with it directly.
 * The public surface is [com.rajjaviya.guideflow.api.GuideFlowBuilder].
 *
 * ## Lifecycle
 * The controller registers itself as a [DefaultLifecycleObserver] on the host's
 * [LifecycleOwner]. When the host is destroyed the tour is automatically cancelled,
 * preventing overlay and listener leaks.
 *
 * ## State machine
 * ```
 * Idle ──start()──► Active ──next() (last step)──► Completed
 *                     │
 *                  skip() ──────────────────────► Dismissed
 *                     │
 *                  pause() ──────────────────────► Paused
 *                     │
 *                  resume() ◄────────────────────── Paused
 *                     │
 *              next() / previous()
 *                     │
 *                  Active (different index)
 * ```
 *
 * @param session        The [TourSession] this controller drives.
 * @param lifecycleOwner The [LifecycleOwner] of the host component.
 * @param listener       Optional [TourListener] for lifecycle callbacks.
 */
internal class TourController(
    private val session: TourSession,
    lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val listener: TourListener?,
) {

    private val _state = MutableStateFlow<TourState>(TourState.Idle)

    /** Observe tour state changes from the overlay / UI layer. */
    val state: StateFlow<TourState> = _state.asStateFlow()

    /** Current step index, kept in sync with [_state]. */
    private var currentIndex = 0

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                // Host is destroyed — cancel silently without firing listener callbacks.
                _state.value = TourState.Idle
            }
        })
    }

    // -------------------------------------------------------------------------
    // Public control API
    // -------------------------------------------------------------------------

    /** Starts the tour from step 0. No-op if already running. */
    fun start() {
        if (_state.value !is TourState.Idle) return
        currentIndex = 0
        emitActive()
        listener?.onTourStarted(session)
        listener?.onStepVisible(currentIndex, session.totalSteps)
    }

    /**
     * Advances to the next step.
     * If the current step is the last one, the tour completes.
     */
    fun next() {
        val active = _state.value as? TourState.Active ?: return
        listener?.onStepCompleted(active.currentIndex)

        if (active.isLastStep) {
            complete()
        } else {
            currentIndex++
            emitActive()
            listener?.onStepVisible(currentIndex, session.totalSteps)
        }
    }

    /**
     * Goes back one step. No-op on the first step.
     */
    fun previous() {
        if (_state.value !is TourState.Active) return
        if (currentIndex == 0) return
        currentIndex--
        emitActive()
        listener?.onStepVisible(currentIndex, session.totalSteps)
    }

    /**
     * Pauses the tour. Useful when the app temporarily leaves foreground.
     * The overlay should be hidden (not destroyed) while paused.
     */
    fun pause() {
        if (_state.value !is TourState.Active) return
        _state.value = TourState.Paused(session)
    }

    /**
     * Resumes a paused tour at the same step index.
     */
    fun resume() {
        if (_state.value !is TourState.Paused) return
        emitActive()
        listener?.onStepVisible(currentIndex, session.totalSteps)
    }

    /**
     * Dismisses the tour at the current step without completing it.
     */
    fun skip() {
        if (_state.value is TourState.Idle) return
        val atIndex = currentIndex
        _state.value = TourState.Dismissed(session.tourId, atIndex)
        listener?.onTourDismissed(atIndex)
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private fun complete() {
        val id = session.tourId
        if (id != null) {
            TourPreferences(context).markCompleted(id)
        }
        _state.value = TourState.Completed(session.tourId)
        listener?.onTourCompleted(session)
    }

    private fun emitActive() {
        _state.value = TourState.Active(
            session = session,
            currentIndex = currentIndex,
            totalSteps = session.totalSteps,
        )
    }
}
