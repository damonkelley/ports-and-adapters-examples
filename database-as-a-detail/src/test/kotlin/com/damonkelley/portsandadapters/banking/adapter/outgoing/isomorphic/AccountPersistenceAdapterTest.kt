package com.damonkelley.portsandadapters.banking.adapter.outgoing.isomorphic

import com.damonkelley.portsandadapters.banking.domain.account
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal
import java.util.UUID

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountPersistenceAdapterTest(@Autowired val jpa: AccountsTable) {
    @Test
    fun `it may not find an account`() {
        val adapter = AccountPersistenceAdapter(jpa)

        assertEquals(null, adapter.findById(UUID.randomUUID()))
    }

    @Test
    fun `it finds and saves and account`() {
        val checking = account(name = "Checking", balance = BigDecimal("130"))

        val adapter = AccountPersistenceAdapter(jpa)
        val savedAccount = adapter.save(checking)
        val foundAccount = adapter.findById(savedAccount.id)

        assertEquals(savedAccount, foundAccount)
    }
}