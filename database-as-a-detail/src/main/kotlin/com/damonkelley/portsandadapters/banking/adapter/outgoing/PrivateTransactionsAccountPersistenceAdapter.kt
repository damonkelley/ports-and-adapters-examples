package com.damonkelley.portsandadapters.banking.adapter.outgoing

import com.damonkelley.portsandadapters.banking.application.IntraBankTransfer
import com.damonkelley.portsandadapters.banking.domain.Account
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.transaction.Transactional

@Repository
interface AccountsTable : CrudRepository<PrivateTransactionsAccountPersistenceAdapter.AccountRecord, UUID>

@Repository
interface TransactionsTable : CrudRepository<PrivateTransactionsAccountPersistenceAdapter.Transaction, UUID> {
    fun findAllByAccountId(accountId: UUID): Collection<PrivateTransactionsAccountPersistenceAdapter.Transaction>

    @Modifying
    @Query(
        value = """
            INSERT INTO transactions("id", "account_id", "amount")
            (
                SELECT gen_random_uuid(), :accountId, :balance - SUM("amount")
                    FROM transactions
                    WHERE account_id = :accountId
                    GROUP BY account_id
                    HAVING (:balance - SUM("amount")) != 0
                UNION 
                SELECT gen_random_uuid(), :accountId, :balance
                    WHERE NOT EXISTS(SELECT 1 FROM transactions WHERE account_id = :accountId)
            )
        """, nativeQuery = true
    )
    @Transactional
    fun record(@Param("accountId") accountId: UUID, @Param("balance") balance: BigDecimal)

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.accountId = :accountId")
    fun balanceFor(@Param("accountId") accountId: UUID): BigDecimal
}

class PrivateTransactionsAccountPersistenceAdapter(
    private val accountsTable: AccountsTable,
    private val transactionsTable: TransactionsTable
) : IntraBankTransfer.Repository {

    @Entity
    data class AccountRecord(
        @Id
        val id: UUID,
        val name: String,
    )

    @Entity(name = "Transaction")
    @Table(name = "transactions")
    data class Transaction(
        @Id
        val id: UUID,

        @Column(name = "accountId")
        val accountId: UUID,

        @Column(name = "amount")
        val amount: BigDecimal
    )

    override fun findById(id: UUID): Account? {
        return accountsTable.findById(id)
            .map { record -> record to transactionsTable.balanceFor(id) }
            .map { (record, balance) -> Account(id = record.id, name = record.name, balance = balance) }
            .orElse(null)
    }

    override fun save(account: Account): Account {
        accountsTable.save(AccountRecord(id = account.id, name = account.name))
        transactionsTable.record(accountId = account.id, balance = account.balance)
        return account
    }
}