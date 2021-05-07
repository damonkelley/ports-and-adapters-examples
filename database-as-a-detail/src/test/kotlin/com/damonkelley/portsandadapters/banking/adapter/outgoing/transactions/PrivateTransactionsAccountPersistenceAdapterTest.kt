package com.damonkelley.portsandadapters.banking.adapter.outgoing.transactions

import com.damonkelley.portsandadapters.banking.domain.account
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal
import java.util.UUID

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PrivateTransactionsAccountPersistenceAdapterTest(
    @Autowired val accountsTable: AccountsTable,
    @Autowired val transactionsTable: TransactionsTable,
) {

    val adapter = PrivateTransactionsAccountPersistenceAdapter(accountsTable, transactionsTable)

    @Test
    fun `it may not find an account`() {
        Assertions.assertEquals(null, adapter.findById(UUID.randomUUID()))
    }

    @Test
    fun `it finds and saves and account`() {
        val checking = account(name = "Checking", balance = BigDecimal("130.00"))

        val savedAccount = adapter.save(checking)
        val foundAccount = adapter.findById(savedAccount.id)

        Assertions.assertEquals(savedAccount, foundAccount)
    }

    @Test
    fun `it does not add a new transaction when the balance remains the same`() {
        val checking = account(name = "Checking", balance = BigDecimal("130.00"))

        adapter.apply {
            save(checking)
            save(checking)
        }

        val expectedTransactionAmounts = listOf(
            BigDecimal("130.00")
        )

        val actualTransactionAmounts = transactionsTable.findAllByAccountId(checking.id).map { it.amount }

        Assertions.assertEquals(expectedTransactionAmounts, actualTransactionAmounts)
    }

    @Test
    fun `it adds a new transaction when the balance changes`() {
        val checking = account(name = "Checking", balance = BigDecimal("130.00"))

        adapter.apply {
            save(checking)
            save(checking.copy(balance = BigDecimal("140")))
        }

        val expectedTransactionAmounts = listOf(
            BigDecimal("130.00"),
            BigDecimal("10.00")
        )

        val actualTransactionAmounts = transactionsTable.findAllByAccountId(checking.id).map { it.amount }

        Assertions.assertEquals(expectedTransactionAmounts, actualTransactionAmounts)
    }

    @Test
    fun `it will record negative transaction amounts`() {
        val checking = account(name = "Checking", balance = BigDecimal("130.00"))

        adapter.apply {
            save(checking)
            save(checking.copy(balance = BigDecimal("100.00")))
        }

        val expectedTransactionAmounts = listOf(
            BigDecimal("130.00"),
            BigDecimal("-30.00")
        )

        val actualTransactionAmounts = transactionsTable.findAllByAccountId(checking.id).map { it.amount }

        Assertions.assertEquals(expectedTransactionAmounts, actualTransactionAmounts)
    }
}