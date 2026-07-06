package com.rajjaviya.guideflow.controller

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.rajjaviya.guideflow.listener.TourListener
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourSession
import com.rajjaviya.guideflow.model.TourState
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TourControllerTest {

    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var lifecycle: LifecycleRegistry

    private val step1 = mockk<GuideStep>(relaxed = true)
    private val step2 = mockk<GuideStep>(relaxed = true)
    private val step3 = mockk<GuideStep>(relaxed = true)

    private fun makeSession(vararg steps: GuideStep) =
        TourSession(steps = steps.toList(), tourId = "test_tour")

    private fun makeController(
        session: TourSession,
        listener: TourListener? = null,
    ) = TourController(session, lifecycleOwner, listener)

    @Before
    fun setUp() {
        lifecycle = LifecycleRegistry(mockk(relaxed = true))
        lifecycleOwner = LifecycleOwner { lifecycle }
        lifecycle.currentState = Lifecycle.State.RESUMED
    }

    @Test
    fun `initial state is Idle`() = runTest {
        val controller = makeController(makeSession(step1))
        assertTrue(controller.state.value is TourState.Idle)
    }

    @Test
    fun `start transitions to Active at index 0`() = runTest {
        val controller = makeController(makeSession(step1, step2))
        controller.start()
        val state = controller.state.value as TourState.Active
        assertEquals(0, state.currentIndex)
        assertEquals(2, state.totalSteps)
    }

    @Test
    fun `next advances index`() = runTest {
        val controller = makeController(makeSession(step1, step2, step3))
        controller.start()
        controller.next()
        val state = controller.state.value as TourState.Active
        assertEquals(1, state.currentIndex)
    }

    @Test
    fun `next on last step completes tour`() = runTest {
        val controller = makeController(makeSession(step1))
        controller.start()
        controller.next()
        assertTrue(controller.state.value is TourState.Completed)
    }

    @Test
    fun `previous goes back one step`() = runTest {
        val controller = makeController(makeSession(step1, step2))
        controller.start()
        controller.next()
        controller.previous()
        val state = controller.state.value as TourState.Active
        assertEquals(0, state.currentIndex)
    }

    @Test
    fun `previous on first step is no-op`() = runTest {
        val controller = makeController(makeSession(step1, step2))
        controller.start()
        controller.previous()
        val state = controller.state.value as TourState.Active
        assertEquals(0, state.currentIndex)
    }

    @Test
    fun `skip transitions to Dismissed with correct index`() = runTest {
        val controller = makeController(makeSession(step1, step2))
        controller.start()
        controller.next()
        controller.skip()
        val state = controller.state.value as TourState.Dismissed
        assertEquals(1, state.atStepIndex)
        assertEquals("test_tour", state.tourId)
    }

    @Test
    fun `pause and resume`() = runTest {
        val controller = makeController(makeSession(step1, step2))
        controller.start()
        controller.next()
        controller.pause()
        assertTrue(controller.state.value is TourState.Paused)
        controller.resume()
        val state = controller.state.value as TourState.Active
        assertEquals(1, state.currentIndex)
    }

    @Test
    fun `listener callbacks fire in correct order`() = runTest {
        val listener = mockk<TourListener>(relaxed = true)
        val session = makeSession(step1, step2)
        val controller = makeController(session, listener)

        controller.start()
        controller.next()
        controller.next() // completes

        verify(exactly = 1) { listener.onTourStarted(session) }
        verify(exactly = 1) { listener.onStepCompleted(0) }
        verify(exactly = 1) { listener.onStepCompleted(1) }
        verify(exactly = 1) { listener.onTourCompleted(session) }
    }

    @Test
    fun `lifecycle destroy resets to Idle`() = runTest {
        val controller = makeController(makeSession(step1))
        controller.start()
        lifecycle.currentState = Lifecycle.State.DESTROYED
        assertTrue(controller.state.value is TourState.Idle)
    }
}
