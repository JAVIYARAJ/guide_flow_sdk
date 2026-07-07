package com.rajjaviya.guideflow.parser

import android.app.Activity
import android.util.Log
import android.view.View
import com.rajjaviya.guideflow.animation.AnimationType
import com.rajjaviya.guideflow.host.TourHost
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.spotlight.SpotlightShape
import com.rajjaviya.guideflow.tooltip.TooltipPosition
import org.json.JSONArray
import org.json.JSONObject

/**
 * Parses JSON payloads into GuideStep instances.
 * Enables remote configuration of tours.
 */
internal object TourJsonParser {

    private const val TAG = "GuideFlow.JsonParser"

    /**
     * Parses a JSON string containing an array of steps.
     * 
     * @param jsonString The raw JSON payload.
     * @param host The [TourHost] used to search for Views.
     * @param fallbackProvider Optional lambda to manually resolve complex View IDs.
     * @return A list of valid [GuideStep]s.
     */
    @Suppress("TooGenericExceptionCaught")
    fun parseSteps(
        jsonString: String,
        host: TourHost,
        fallbackProvider: ((String) -> View?)? = null
    ): List<GuideStep> {
        val steps = mutableListOf<GuideStep>()
        
        try {
            val root = JSONObject(jsonString)
            val jsonSteps = root.optJSONArray("steps") ?: JSONArray(jsonString)
            
            for (i in 0 until jsonSteps.length()) {
                val stepObj = jsonSteps.getJSONObject(i)
                
                val targetId = stepObj.getString("targetId")
                val targetView = resolveView(targetId, host, fallbackProvider)
                
                if (targetView == null) {
                    Log.e(TAG, "Skipping step $i: Could not find view for targetId '$targetId'")
                    continue
                }

                val title = stepObj.optString("title", "")
                val description = stepObj.optString("description", "")
                
                val tooltipPosStr = stepObj.optString("tooltipPosition", "AUTO")
                val tooltipPosition = parseEnum(tooltipPosStr, TooltipPosition.AUTO)
                
                val animTypeStr = stepObj.optString("animationType", "FADE")
                val animationType = parseEnum(animTypeStr, AnimationType.FADE)
                
                val shapeStr = stepObj.optString("spotlightShape", "")
                val spotlightShape = if (shapeStr.isNotEmpty()) {
                    parseEnum(shapeStr, SpotlightShape.ROUNDED_RECT)
                } else null
                
                val pointerOffset = stepObj.optDouble("pointerOffset", 0.5).toFloat()
                
                // Button visibility
                val showNextButton = stepObj.optBoolean("showNextButton", true)
                val showSkipButton = stepObj.optBoolean("showSkipButton", true)

                // Custom Labels (fall back to GuideStep defaults if not present)
                val nextButtonLabel = stepObj.optString("nextButtonLabel", "Next")
                val skipButtonLabel = stepObj.optString("skipButtonLabel", "Skip")
                val previousButtonLabel = stepObj.optString("previousButtonLabel", "Back")

                val tag = stepObj.optString("tag", null).takeIf { it.isNotEmpty() }

                steps.add(
                    GuideStep(
                        targetView = targetView,
                        title = title,
                        description = description,
                        tooltipPosition = tooltipPosition,
                        animationType = animationType,
                        showNextButton = showNextButton,
                        nextButtonLabel = nextButtonLabel,
                        showSkipButton = showSkipButton,
                        skipButtonLabel = skipButtonLabel,
                        previousButtonLabel = previousButtonLabel,
                        pointerOffset = pointerOffset,
                        spotlightShape = spotlightShape,
                        tag = tag
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse JSON tours: ${e.message}", e)
        }
        
        return steps
    }

    @Suppress("ReturnCount")
    private fun resolveView(
        targetId: String,
        host: TourHost,
        fallbackProvider: ((String) -> View?)?
    ): View? {
        val context = host.getContext()
        val rootView = host.getRootView()

        // 1. Try resolving using Android's resource identifier system
        val resId = context.resources.getIdentifier(targetId, "id", context.packageName)
        if (resId != 0) {
            val foundView = rootView.findViewById<View>(resId)
            // If host is an Activity, sometimes rootView doesn't catch everything, try direct lookup
            if (foundView == null && context is Activity) {
                context.findViewById<View>(resId)?.let { return it }
            } else if (foundView != null) {
                return foundView
            }
        }

        // 2. If not found or no resource ID exists, use the fallback provider
        return fallbackProvider?.invoke(targetId)
    }

    @Suppress("SwallowedException")
    private inline fun <reified T : Enum<T>> parseEnum(value: String, default: T): T {
        return try {
            java.lang.Enum.valueOf(T::class.java, value.uppercase())
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Invalid enum value '$value' for ${T::class.java.simpleName}. Using default $default.")
            default
        }
    }
}
