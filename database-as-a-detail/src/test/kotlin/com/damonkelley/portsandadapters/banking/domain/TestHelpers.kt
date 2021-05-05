package com.damonkelley.portsandadapters.banking.domain

import java.math.BigDecimal
import java.util.UUID

fun account(name: String = "", balance: BigDecimal = BigDecimal.ZERO): Account {
    return Account(
        id = UUID.randomUUID(),
        name = name,
        balance = balance
    )
}