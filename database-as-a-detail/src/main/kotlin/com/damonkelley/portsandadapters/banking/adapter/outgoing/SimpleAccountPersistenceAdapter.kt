package com.damonkelley.portsandadapters.banking.adapter.outgoing

import com.damonkelley.portsandadapters.banking.application.IntraBankTransfer
import com.damonkelley.portsandadapters.banking.domain.Account
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Repository
interface JPAAccountRecordDatabase : CrudRepository<SimpleAccountPersistenceAdapter.AccountRecord, UUID>

class SimpleAccountPersistenceAdapter(private val database: JPAAccountRecordDatabase) : IntraBankTransfer.Repository {

    @Entity
    data class AccountRecord(
        @Id
        @Column(name = "id")
        val id: UUID,

        @Column(name = "name")
        val name: String,

        @Column(name = "balance")
        val balance: BigDecimal,
    )

    override fun findById(id: UUID): Account? {
        val record = database.findById(id).orElse(null)

        return record?.let { found ->
            Account(id = found.id, name = found.name, balance = found.balance)
        }
    }

    override fun save(account: Account): Account {
        return AccountRecord(id = account.id, name = account.name, balance = account.balance)
            .let { database.save(it) }
            .let { record -> Account(id = record.id, name = record.name, balance = record.balance) }
    }
}