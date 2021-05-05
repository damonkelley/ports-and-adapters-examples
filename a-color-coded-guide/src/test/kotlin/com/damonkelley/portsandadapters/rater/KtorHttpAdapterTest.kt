package com.damonkelley.todo

import com.damonkelley.portsandadapters.rater.KtorHttpAdapter
import com.damonkelley.portsandadapters.rater.RatingUseCase
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import java.math.BigDecimal

class KtorHttpAdapterTest {
    @Test
    fun `it serves the rate and the rate applied to the value`() {
        withTestApplication({ KtorHttpAdapter(TestRatingApplication()).configure(this) }) {
            handleRequest(HttpMethod.Get, "/100").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Rate: 1, Result: 100", response.content)
            }
        }
    }
}

class TestRatingApplication : RatingUseCase {
    override fun rateAndResult(value: BigDecimal): RatingUseCase.Result {
        return RatingUseCase.Result(
            rate = BigDecimal("1"),
            applied = value
        )
    }
}