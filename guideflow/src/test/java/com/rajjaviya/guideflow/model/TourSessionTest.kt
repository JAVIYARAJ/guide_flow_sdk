package com.rajjaviya.guideflow.model

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourSession
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TourSessionTest {

    private val step1 = mockk<GuideStep>(relaxed = true) {
        io.mockk.every { tag } returns "step1"
    }
    private val step2 = mockk<GuideStep>(relaxed = true) {
        io.mockk.every { tag } returns "step2"
    }
    private val step3 = mockk<GuideStep>(relaxed = true) {
        io.mockk.every { tag } returns "step3"
    }

    @org.junit.Before
    fun setUp() {
        io.mockk.mockkStatic(android.graphics.Color::class)
        io.mockk.every { android.graphics.Color.parseColor(any()) } answers { 0 }
    }

    @org.junit.After
    fun tearDown() {
        io.mockk.unmockkStatic(android.graphics.Color::class)
    }

    @Test
    fun `session initializes correctly`() {
        val session = TourSession(initialSteps = listOf(step1, step2))
        
        assertEquals(2, session.totalSteps)
        assertEquals(step1, session.stepAt(0))
        assertEquals(step2, session.stepAt(1))
        assertNull(session.stepAt(2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `session throws if steps are empty`() {
        TourSession(initialSteps = emptyList())
    }

    @Test
    fun `addStepAfter inserts step at correct index`() {
        val session = TourSession(initialSteps = listOf(step1, step3))
        
        // Insert step2 after step1
        session.addStepAfter("step1", step2)
        
        assertEquals(3, session.totalSteps)
        assertEquals(step1, session.stepAt(0))
        assertEquals(step2, session.stepAt(1))
        assertEquals(step3, session.stepAt(2))
    }

    @Test
    fun `addStepAfter appends to end if tag not found`() {
        val session = TourSession(initialSteps = listOf(step1, step2))
        
        // Try inserting after unknown tag
        session.addStepAfter("unknown", step3)
        
        assertEquals(3, session.totalSteps)
        assertEquals(step3, session.stepAt(2)) // Appended at the end
    }

    @Test
    fun `removeStep removes step by tag`() {
        val session = TourSession(initialSteps = listOf(step1, step2, step3))
        
        session.removeStep("step2")
        
        assertEquals(2, session.totalSteps)
        assertEquals(step1, session.stepAt(0))
        assertEquals(step3, session.stepAt(1))
    }
}
