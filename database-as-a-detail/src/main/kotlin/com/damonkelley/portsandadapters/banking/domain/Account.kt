package com.damonkelley.portsandadapters.banking.domain

import java.math.BigDecimal
import java.util.UUID

data class Account(val id: UUID, val name: String, var balance: BigDecimal) {
    fun transfer(toAccount: Account, amount: BigDecimal) {
        toAccount.debit(amount)
        this.credit(amount)
    }

    private fun credit(amount: BigDecimal) {
        balance -= amount
    }

    private fun debit(amount: BigDecimal) {
        balance += amount
    }
}