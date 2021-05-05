package com.damonkelley.portsandadapters.banking.adapter.outgoing

import com.damonkelley.portsandadapters.banking.domain.account
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal
import java.util.UUID

@DataJpaTest
class SimpleAccountPersistenceAdapterTest(@Autowired val jpa: JPAAccountRecordDatabase) {
    @Test
    fun `it may not find an account`() {
        val adapter = SimpleAccountPersistenceAdapter(jpa)

        Assertions.assertEquals(null, adapter.findById(UUID.randomUUID()))
    }
    @Test
    fun `it finds and saves and account`() {
        val checking = account(name = "Checking", balance = BigDecimal("130"))

        val adapter = SimpleAccountPersistenceAdapter(jpa)
        val savedAccount = adapter.save(checking)
        val foundAccount = adapter.findById(savedAccount.id)

        Assertions.assertEquals(savedAccount, foundAccount)
    }
}