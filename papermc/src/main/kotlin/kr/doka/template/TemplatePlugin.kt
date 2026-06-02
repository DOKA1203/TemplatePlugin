package kr.doka.template

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kr.doka.template.core.database.DatabaseManager
import org.bukkit.plugin.java.JavaPlugin

class TemplatePlugin : JavaPlugin() {
    val pluginScope: CoroutineScope =
        CoroutineScope(
            SupervisorJob() +
                Dispatchers.Default +
                CoroutineName("TemplatePlugin-Scope") +
                CoroutineExceptionHandler { _, e -> logger.severe("TemplatePlugin-Scope - Coroutine error: $e") },
        )

    companion object {
        lateinit var instance: TemplatePlugin
            private set

        val pluginScope: CoroutineScope
            get() = instance.pluginScope
    }

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        initDatabase()
    }

    override fun onDisable() {
        pluginScope.cancel()
    }

    private fun initDatabase() {
        val db =
            config.getConfigurationSection("database") ?: run {
                logger.severe("Missing 'database' section in config.yml")
                server.pluginManager.disablePlugin(this)
                return
            }

        dataFolder.mkdirs()

        when (val type = db.getString("type", "sqlite")!!.lowercase()) {
            "sqlite" -> {
                val file = db.getString("file", "database.db")!!
                DatabaseManager.initialize(
                    url = "jdbc:sqlite:${dataFolder.absolutePath}/$file",
                    driver = "org.sqlite.JDBC",
                )
            }
            "mysql" -> {
                DatabaseManager.initialize(
                    url = "jdbc:mysql://${db.getString("host")}:${db.getInt("port")}/${db.getString("name")}",
                    driver = "com.mysql.cj.jdbc.Driver",
                    user = db.getString("username", "")!!,
                    password = db.getString("password", "")!!,
                )
            }
            "postgresql" -> {
                DatabaseManager.initialize(
                    url = "jdbc:postgresql://${db.getString("host")}:${db.getInt("port")}/${db.getString("name")}",
                    driver = "org.postgresql.Driver",
                    user = db.getString("username", "")!!,
                    password = db.getString("password", "")!!,
                )
            }
            else -> {
                logger.severe("Unknown database type: $type")
                server.pluginManager.disablePlugin(this)
            }
        }
    }
}
