package com.damonkelley.portsandadapters.banking.application

import com.damonkelley.portsandadapters.banking.domain.Account
import java.math.BigDecimal
import java.util.UUID

class IntraBankTransfer(private val repository: Repository) {
    interface Repository {
        fun findById(id: UUID): Account?
        fun save(account: Account): Account
    }

    fun transfer(toAccountId: UUID, fromAccountId: UUID, amount: BigDecimal): Pair<Account, Account> {
        val fromAccount = repository.findById(fromAccountId) ?: throw Error("Source account does not exist")
        val toAccount = repository.findById(toAccountId) ?: throw Error("Destination account does not exist")

        fromAccount.transfer(toAccount, amount)

        return repository.save(fromAccount) to repository.save(toAccount)
    }
}