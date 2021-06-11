package com.damonkelley.portsandadapters.rater

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.math.BigDecimal


interface RatingUseCase {
    fun rateAndResult(value: BigDecimal): Result

    data class Result(val rate: BigDecimal, val applied: BigDecimal)
}

class RatingApplication(private val rater: RatingProvider) : RatingUseCase {
    interface RatingProvider {
        fun rateFor(value: BigDecimal): BigDecimal
    }

    override fun rateAndResult(value: BigDecimal): RatingUseCase.Result {
        val rate = rater.rateFor(value)

        return RatingUseCase.Result(
            rate = rate,
            applied = value * rate
        )
    }
}

class InCodeRater : RatingApplication.RatingProvider {
    override fun rateFor(value: BigDecimal): BigDecimal = when {
        value < BigDecimal("100") -> BigDecimal("1.01")
        else -> BigDecimal("1.5")
    }
}

class KtorHttpAdapter(private val ratingApplication: RatingUseCase) {
    fun configure(ktor: Application) {
        ktor.routing {
            get("/{valueToRate}") {
                val valueToRate = call.parameters["valueToRate"].let(::BigDecimal)

                val result = ratingApplication.rateAndResult(valueToRate)

                call.respondText("Rate: ${result.rate}, Result: ${result.applied}")
            }
        }
    }
}

fun main() {
    val rater = InCodeRater()
    val application = RatingApplication(rater)

    embeddedServer(Netty, port = 8000) {
        KtorHttpAdapter(application).configure(this)
    }.start(wait = true)
}