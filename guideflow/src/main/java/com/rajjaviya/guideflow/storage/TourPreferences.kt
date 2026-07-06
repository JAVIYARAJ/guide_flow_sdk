package com.rajjaviya.guideflow.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Handles persisting whether a specific tour has been completed.
 * This prevents the SDK from showing the same onboarding tour to a user multiple times.
 */
internal class TourPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE,
    )

    /**
     * Marks the given [tourId] as completed.
     */
    fun markCompleted(tourId: String) {
        prefs.edit().putBoolean(tourId, true).apply()
    }

    /**
     * Checks if the given [tourId] has already been completed.
     */
    fun isCompleted(tourId: String): Boolean {
        return prefs.getBoolean(tourId, false)
    }

    /**
     * Clears completion status for a specific tour. Useful for testing or resetting state.
     */
    fun clear(tourId: String) {
        prefs.edit().remove(tourId).apply()
    }

    /**
     * Clears all stored tour completion data.
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "GuideFlow_Preferences"
    }
}
