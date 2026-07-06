package com.rajjaviya.guideflow

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import com.rajjaviya.guideflow.controller.TourController
import com.rajjaviya.guideflow.listener.TourListener
import com.rajjaviya.guideflow.model.GuideStep
import com.rajjaviya.guideflow.model.TourConfig
import com.rajjaviya.guideflow.model.TourSession
import com.rajjaviya.guideflow.model.TourState
import com.rajjaviya.guideflow.model.TourTheme
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TourControllerTest {

    private lateinit var controller: TourController
    private lateinit var lifecycleOwner: TestLifecycleOwner
    private lateinit var listener: TourListener
    private lateinit var context: Context
    private lateinit var session: TourSession

    @Before
    fun setup() {
        lifecycleOwner = TestLifecycleOwner(Lifecycle.State.RESUMED)
        listener = mockk(relaxed = true)
        context = mockk(relaxed = true)
        
        session = TourSession(
            tourId = "test_tour",
            steps = listOf(
                GuideStep(mockk(relaxed = true), title = "Step 1"),
                GuideStep(mockk(relaxed = true), title = "Step 2")
            ),
            theme = TourTheme.light(),
            config = TourConfig.default()
        )

        controller = TourController(
            session = session,
            lifecycleOwner = lifecycleOwner,
            context = context,
            listener = listener
        )
    }

    @Test
    fun `initial state is Idle`() = runTest {
        val state = controller.state.first()
        assertTrue(state is TourState.Idle)
    }

    @Test
    fun `start() moves to Active state and triggers listener`() = runTest {
        controller.start()
        
        val state = controller.state.value as TourState.Active
        assertEquals(0, state.currentIndex)
        
        verify { listener.onTourStarted(session) }
        verify { listener.onStepVisible(0, 2) }
    }

    @Test
    fun `next() advances to second step`() = runTest {
        controller.start()
        controller.next()
        
        val state = controller.state.value as TourState.Active
        assertEquals(1, state.currentIndex)
        
        verify { listener.onStepCompleted(0) }
        verify { listener.onStepVisible(1, 2) }
    }

    @Test
    fun `next() on last step completes tour`() = runTest {
        controller.start()
        controller.next() // To step 2
        controller.next() // Complete
        
        assertTrue(controller.state.value is TourState.Completed)
        verify { listener.onTourCompleted(session) }
    }

    @Test
    fun `skip() dismisses tour`() = runTest {
        controller.start()
        controller.skip()
        
        assertTrue(controller.state.value is TourState.Dismissed)
        verify { listener.onTourDismissed(0) }
    }
}
