package com.rajjaviya.guideflow.parser

import android.content.Context
import android.view.View
import com.rajjaviya.guideflow.host.TourHost
import com.rajjaviya.guideflow.animation.AnimationType
import com.rajjaviya.guideflow.spotlight.SpotlightShape
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TourJsonParserTest {

    private val mockLifecycleOwner = mockk<androidx.lifecycle.LifecycleOwner>(relaxed = true)
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockRootView = mockk<android.view.ViewGroup>(relaxed = true)
    private val mockTargetView = mockk<View>(relaxed = true)
    
    private val host = object : TourHost {
        override fun getContext(): Context = mockContext
        override fun getRootView(): android.view.ViewGroup = mockRootView
        override fun getLifecycleOwner(): androidx.lifecycle.LifecycleOwner = mockLifecycleOwner
    }

    @Test
    fun `parse valid json with premium properties`() {
        val json = """
            { "steps": [
              {
                "targetId": "my_button",
                "title": "Welcome",
                "description": "Click here",
                "animationType": "SPRING_PHYSICS",
                "spotlightShape": "OVAL",
                "pointerOffset": 0.75,
                "tag": "first_step"
              }
            ] }
        """.trimIndent()

        val steps = TourJsonParser.parseSteps(
            jsonString = json,
            host = host,
            fallbackProvider = { mockTargetView }
        )

        assertEquals(1, steps.size)
        val step = steps[0]
        assertEquals("Welcome", step.title)
        assertEquals("Click here", step.description)
        assertEquals(AnimationType.SPRING_PHYSICS, step.animationType)
        assertEquals(SpotlightShape.OVAL, step.spotlightShape)
        assertEquals(0.75f, step.pointerOffset)
        assertEquals("first_step", step.tag)
    }

    @Test
    fun `parse invalid enum falls back to default`() {
        val json = """
            { "steps": [
              {
                "targetId": "my_button",
                "animationType": "INVALID_ANIMATION",
                "spotlightShape": "WEIRD_SHAPE"
              }
            ] }
        """.trimIndent()

        val steps = TourJsonParser.parseSteps(
            jsonString = json,
            host = host,
            fallbackProvider = { mockTargetView }
        )

        assertEquals(1, steps.size)
        val step = steps[0]
        
        // Default fallbacks
        assertEquals(AnimationType.FADE, step.animationType)
        assertEquals(SpotlightShape.ROUNDED_RECT, step.spotlightShape) 
    }

    @Test
    fun `parse empty shape string returns null`() {
        val json = """
            { "steps": [
              {
                "targetId": "my_button",
                "spotlightShape": ""
              }
            ] }
        """.trimIndent()

        val steps = TourJsonParser.parseSteps(
            jsonString = json,
            host = host,
            fallbackProvider = { mockTargetView }
        )

        assertEquals(1, steps.size)
        assertNull(steps[0].spotlightShape)
    }
}
