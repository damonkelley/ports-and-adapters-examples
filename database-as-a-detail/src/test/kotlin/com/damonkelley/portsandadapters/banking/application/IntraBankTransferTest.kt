package com.damonkelley.portsandadapters.banking.application

import com.damonkelley.portsandadapters.banking.domain.account
import com.damonkelley.portsandadapters.banking.domain.Account
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID

class IntraBankTransferTest {
    private val fromAccount = account()
    private val toAccount = account()
    private val repository = InMemoryRepository(mutableListOf(toAccount, fromAccount))

    @Test
    fun `it transfers the amount between accounts`() {
        val amount = BigDecimal("3")

        IntraBankTransfer(repository)
            .transfer(toAccountId = toAccount.id, fromAccountId = fromAccount.id, amount = amount)

        assertEquals(repository.findById(toAccount.id)!!.balance, toAccount.balance + amount)
        assertEquals(repository.findById(fromAccount.id)!!.balance, fromAccount.balance - amount)
    }

    @Test
    fun `it throws if the source account does not exist`() {
        assertThrows<Error> {
            IntraBankTransfer(repository)
                .transfer(toAccountId = toAccount.id, fromAccountId = UUID.randomUUID(), amount = BigDecimal.ZERO)
        }
    }

    @Test
    fun `it throws if the destination account does not exist`() {
        assertThrows<Error> {
            IntraBankTransfer(repository)
                .transfer(toAccountId = UUID.randomUUID(), fromAccountId = fromAccount.id, amount = BigDecimal.ZERO)
        }
    }
}

class InMemoryRepository(private val accounts: MutableList<Account>) : IntraBankTransfer.Repository {
    override fun findById(id: UUID): Account? {
        return accounts.firstOrNull { it.id == id }?.copy()
    }

    override fun save(account: Account): Account {
        accounts.apply {
            removeIf { it.id == account.id }
            add(account)
        }

        return account
    }
}