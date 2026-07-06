package com.rajjaviya.guideflow.positioning

import android.view.View
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ViewResolverTest {

    private fun mockView(
        attached: Boolean = true,
        laidOut: Boolean = true,
        width: Int = 100,
        height: Int = 50,
        visibility: Int = View.VISIBLE,
    ): View = mockk(relaxed = true) {
        every { isAttachedToWindow } returns attached
        every { isLaidOut } returns laidOut
        every { getWidth() } returns width
        every { getHeight() } returns height
        every { getVisibility() } returns visibility
    }

    @Test
    fun `detectIssue returns null for a ready view`() {
        val view = mockView()
        assertNull(ViewResolver.detectIssue(view))
    }

    @Test
    fun `detectIssue returns NOT_ATTACHED when view is detached`() {
        val view = mockView(attached = false)
        assertEquals(ViewIssue.NOT_ATTACHED, ViewResolver.detectIssue(view))
    }

    @Test
    fun `detectIssue returns NOT_LAID_OUT when view is attached but not laid out`() {
        val view = mockView(laidOut = false)
        assertEquals(ViewIssue.NOT_LAID_OUT, ViewResolver.detectIssue(view))
    }

    @Test
    fun `detectIssue returns ZERO_SIZE when view has zero width`() {
        val view = mockView(width = 0)
        assertEquals(ViewIssue.ZERO_SIZE, ViewResolver.detectIssue(view))
    }

    @Test
    fun `detectIssue returns ZERO_SIZE when view has zero height`() {
        val view = mockView(height = 0)
        assertEquals(ViewIssue.ZERO_SIZE, ViewResolver.detectIssue(view))
    }

    @Test
    fun `detectIssue returns NOT_VISIBLE when view is GONE`() {
        val view = mockView(visibility = View.GONE)
        assertEquals(ViewIssue.NOT_VISIBLE, ViewResolver.detectIssue(view))
    }

    @Test
    fun `detectIssue returns NOT_VISIBLE when view is INVISIBLE`() {
        val view = mockView(visibility = View.INVISIBLE)
        assertEquals(ViewIssue.NOT_VISIBLE, ViewResolver.detectIssue(view))
    }

    @Test
    fun `NOT_ATTACHED takes priority over NOT_LAID_OUT`() {
        val view = mockView(attached = false, laidOut = false)
        assertEquals(ViewIssue.NOT_ATTACHED, ViewResolver.detectIssue(view))
    }
}
