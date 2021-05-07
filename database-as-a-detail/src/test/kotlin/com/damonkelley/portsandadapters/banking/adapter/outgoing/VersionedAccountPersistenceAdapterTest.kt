package com.damonkelley.portsandadapters.banking.adapter.outgoing

import com.damonkelley.portsandadapters.banking.domain.account
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal
import java.util.UUID

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VersionedAccountPersistenceAdapterTest(@Autowired val accountVersionsTable: AccountVersionsTable) {
    lateinit var adapter: VersionedAccountPersistenceAdapter

    @BeforeEach
    fun setup() {
        adapter = VersionedAccountPersistenceAdapter(accountVersionsTable)
    }

    @Test
    fun `it may not find an account`() {
        assertEquals(null, adapter.findById(UUID.randomUUID()))
    }

    @Test
    fun `it finds and saves and account`() {
        val checking = account(name = "Checking", balance = BigDecimal("130"))

        val savedAccount = adapter.save(checking)
        val foundAccount = adapter.findById(savedAccount.id)

        assertEquals(savedAccount, foundAccount)
    }

    @Test
    fun `it finds the latest version of the account`() {
        val checking = account(name = "Checking", balance = BigDecimal("130"))

        adapter.save(checking)
        assertEquals(checking, adapter.findById(checking.id))

        val checkingWithUpdatedBalance = checking.copy(balance = BigDecimal("120"))
        adapter.save(checkingWithUpdatedBalance)

        assertEquals(checkingWithUpdatedBalance, adapter.findById(checking.id))
    }

    @Test
    fun `it does not record new version when the name or balance is unchanged`() {
        val checking = account(name = "Checking", balance = BigDecimal("130"))

        adapter.save(checking)
        assertEquals(1, accountVersionsTable.findAllByAccountId(checking.id).size)

        adapter.save(checking)

        assertEquals(1, accountVersionsTable.findAllByAccountId(checking.id).size)
    }

    @Test
    fun `it records new version when the balance changes`() {
        val checking = account(name = "Checking", balance = BigDecimal("130"))
        val savings = account(name = "Savings", balance = BigDecimal("0"))

        adapter.save(checking)
        assertEquals(1, accountVersionsTable.findAllByAccountId(checking.id).size)

        checking.transfer(savings, BigDecimal.TEN)

        adapter.save(checking)
        assertEquals(2, accountVersionsTable.findAllByAccountId(checking.id).size)
    }

    @Test
    fun `it records new version when the name changes`() {
        val checking = account(name = "Checking", balance = BigDecimal("130"))

        adapter.save(checking)
        assertEquals(1, accountVersionsTable.findAllByAccountId(checking.id).size)

        adapter.save(checking.copy(name = "Banana Stand"))
        assertEquals(2, accountVersionsTable.findAllByAccountId(checking.id).size)

        accountVersionsTable.findAllByAccountId(checking.id).let(::println)
    }
}