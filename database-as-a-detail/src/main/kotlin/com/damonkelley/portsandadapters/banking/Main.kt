package com.damonkelley.portsandadapters.banking

import com.damonkelley.portsandadapters.banking.adapter.incoming.CommandLineIncomingAdapter
import com.damonkelley.portsandadapters.banking.adapter.outgoing.JPAAccountRecordDatabase
import com.damonkelley.portsandadapters.banking.adapter.outgoing.SimpleAccountPersistenceAdapter
import com.damonkelley.portsandadapters.banking.application.IntraBankTransfer
import com.damonkelley.portsandadapters.banking.domain.Account
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.math.BigDecimal
import java.util.UUID

@SpringBootApplication
class Main {
    @Bean
    fun onStartup(database: JPAAccountRecordDatabase): CommandLineRunner {
        return CommandLineRunner {
            val repository = SimpleAccountPersistenceAdapter(database)

            val savings =
                repository.save(Account(id = UUID.randomUUID(), name = "Savings", balance = BigDecimal("100")))
            val checking =
                repository.save(Account(id = UUID.randomUUID(), name = "Checking", balance = BigDecimal("100")))

            val intraBankTransfer = IntraBankTransfer(repository)

            CommandLineIncomingAdapter(intraBankTransfer)
                .execute(fromAccountId = checking.id, toAccountId = savings.id)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
