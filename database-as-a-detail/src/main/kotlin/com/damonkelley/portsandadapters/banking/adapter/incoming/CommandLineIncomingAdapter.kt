package com.damonkelley.portsandadapters.banking.adapter.incoming

import com.damonkelley.portsandadapters.banking.application.IntraBankTransfer
import java.math.BigDecimal
import java.util.UUID

class CommandLineIncomingAdapter(private val intraBankTransfer: IntraBankTransfer) {
    fun execute(fromAccountId: UUID, toAccountId: UUID) {
        val ten = BigDecimal.TEN

        val (fromAccount, toAccount) = intraBankTransfer.transfer(
            toAccountId = toAccountId,
            fromAccountId = fromAccountId,
            amount = ten
        )

        println(
            """
                Transferred $${ten} from ${fromAccount.name} to ${toAccount.name}:w
                
                Balances
                ========
                ${fromAccount.name}: $${fromAccount.balance}
                ${toAccount.name}: ${'$'}${toAccount.balance}
            """.trimIndent()
        )
    }
}