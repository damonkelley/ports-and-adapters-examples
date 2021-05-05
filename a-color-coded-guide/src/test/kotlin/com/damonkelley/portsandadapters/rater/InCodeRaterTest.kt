package com.damonkelley.portsandadapters.rater

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class InCodeRaterTest {
    private val rater = InCodeRater()

    @Test
    fun `when the value is greater than 100, it uses a larger rate`() {
        assertEquals(BigDecimal("1.5"), rater.rateFor(BigDecimal("101")))
    }

    @Test
    fun `when the value is equal to 100, it uses a larger rate`() {
        assertEquals(BigDecimal("1.5"), rater.rateFor(BigDecimal("100")))
    }

    @Test
    fun `when the value is less than 100, it uses a larger rate`() {
        assertEquals(BigDecimal("1.01"), rater.rateFor(BigDecimal("99")))
    }
}