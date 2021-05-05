package com.damonkelley.portsandadapters.banking.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class AccountTest {
    @Test
    fun `an account can transfer an amount to another account`() {
        val checking = Account(id = UUID.randomUUID(), name = "Checking", balance = BigDecimal("20"))
        val savings = Account(id = UUID.randomUUID(), name = "Checking", balance = BigDecimal.ZERO)

        checking.transfer(savings, BigDecimal("5"))

        assertEquals(BigDecimal("15"), checking.balance)
        assertEquals(BigDecimal("5"), savings.balance)
    }
}