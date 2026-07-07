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

    /** Starts the tour. Resumes from the last saved step if applicable. No-op if already running. */
    fun start() {
        if (_state.value !is TourState.Idle) return
        
        val savedIndex = if (session.config.resumeWhereLeftOff) {
            session.tourId?.let { TourPreferences(context).getLastStep(it) } ?: 0
        } else {
            0
        }
        
        currentIndex = findNextValidStepIndex(savedIndex)
        
        if (currentIndex == -1) {
            complete()
            return
        }
        
        emitActive()
        listener?.onTourStarted(session)
        listener?.onStepVisible(currentIndex, session.totalSteps)
    }

    /**
     * Advances to the next step that meets its condition.
     * If no further steps meet their condition, the tour completes.
     */
    fun next() {
        val active = _state.value as? TourState.Active ?: return
        listener?.onStepCompleted(active.currentIndex)

        val nextIndex = findNextValidStepIndex(currentIndex + 1)
        if (nextIndex == -1) {
            complete()
        } else {
            currentIndex = nextIndex
            emitActive()
            listener?.onStepVisible(currentIndex, session.totalSteps)
        }
    }

    /**
     * Goes back to the previous step that meets its condition. No-op if none found.
     */
    fun previous() {
        if (_state.value !is TourState.Active) return
        val prevIndex = findPreviousValidStepIndex(currentIndex - 1)
        if (prevIndex == -1) return
        
        currentIndex = prevIndex
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
        session.tourId?.let { TourPreferences(context).saveLastStep(it, currentIndex) }
        _state.value = TourState.Active(
            session = session,
            currentIndex = currentIndex,
            totalSteps = session.totalSteps,
        )
    }

    private fun findNextValidStepIndex(startIndex: Int): Int {
        for (i in startIndex until session.totalSteps) {
            if (session.stepAt(i)?.condition?.invoke() == true) return i
        }
        return -1
    }

    private fun findPreviousValidStepIndex(startIndex: Int): Int {
        for (i in startIndex downTo 0) {
            if (session.stepAt(i)?.condition?.invoke() == true) return i
        }
        return -1
    }
}
