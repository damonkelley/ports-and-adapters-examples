package com.damonkelley.portsandadapters.banking.adapter.outgoing

import com.damonkelley.portsandadapters.banking.application.IntraBankTransfer
import com.damonkelley.portsandadapters.banking.domain.Account
import org.springframework.data.repository.CrudRepository
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Optional
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

interface AccountVersionsTable : CrudRepository<VersionedAccountPersistenceAdapter.AccountVersion, BigInteger> {
    fun findAllByAccountId(id: UUID): Collection<VersionedAccountPersistenceAdapter.AccountVersion>
    fun findFirstByAccountIdOrderByVersionDesc(id: UUID): Optional<VersionedAccountPersistenceAdapter.AccountVersion>
}

class VersionedAccountPersistenceAdapter(private val accountVersionsTable: AccountVersionsTable) :
    IntraBankTransfer.Repository {

    @Entity
    @Table(name = "account_versions", schema = "account_versioning")
    data class AccountVersion(
        @Id
        @GeneratedValue
        val version: BigInteger? = null,
        val accountId: UUID,
        val name: String,
        val balance: BigDecimal
    )

    override fun findById(id: UUID): Account? {
        return accountVersionsTable.findFirstByAccountIdOrderByVersionDesc(id)
            .map { Account(id = it.accountId, name = it.name, balance = it.balance) }
            .orElse(null)
    }

    override fun save(account: Account): Account {
        findById(account.id)?.let {
            if (account == Account(id = it.id, name = it.name, balance = it.balance)) {
                return account
            }
        }

        return AccountVersion(accountId = account.id, name = account.name, balance = account.balance)
            .let { version -> accountVersionsTable.save(version) }
            .let { version -> Account(id = version.accountId, name = version.name, balance = version.balance) }
    }
}