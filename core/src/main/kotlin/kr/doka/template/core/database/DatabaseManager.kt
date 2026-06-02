package kr.doka.template.core.database

import kr.doka.template.core.user.UserTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseManager {
    fun initialize(
        url: String,
        driver: String,
        user: String = "",
        password: String = "",
    ) {
        Database.connect(url = url, driver = driver, user = user, password = password)
        transaction {
            SchemaUtils.create(UserTable)
        }
    }
}
