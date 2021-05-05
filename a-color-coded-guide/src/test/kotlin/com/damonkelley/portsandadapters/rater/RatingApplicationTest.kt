package com.damonkelley.portsandadapters.rater

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

internal class RatingApplicationTest {
    @Test
    fun `it applies a rate to the value`() {
        val actual = RatingApplication(ConstantRatingProvider(BigDecimal("0.1")))
            .rateAndResult(BigDecimal.valueOf(50))

        val expected = RatingUseCase.Result(rate = BigDecimal("0.1"), BigDecimal("5.0"))

        assertEquals(expected, actual)
    }
}

class ConstantRatingProvider(private val rate: BigDecimal) : RatingApplication.RatingProvider {
    override fun rateFor(value: BigDecimal): BigDecimal = rate
}
