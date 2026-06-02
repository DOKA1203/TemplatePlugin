package kr.doka.template.core.user

import kr.doka.template.api.user.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserRepositoryImplTest {
    private val repository = UserRepositoryImpl()

    companion object {
        init {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        }
    }

    @BeforeTest
    fun setUp() {
        transaction {
            SchemaUtils.create(UserTable)
        }
    }

    @AfterTest
    fun tearDown() {
        transaction {
            SchemaUtils.drop(UserTable)
        }
    }

    @Test
    fun `save and findById`() {
        val user = User(id = UUID.randomUUID(), name = "TestUser")
        repository.save(user)

        val found = repository.findById(user.id)
        assertNotNull(found)
        assertEquals(user.id, found.id)
        assertEquals(user.name, found.name)
    }

    @Test
    fun `findById returns null when user does not exist`() {
        assertNull(repository.findById(UUID.randomUUID()))
    }

    @Test
    fun `findAll returns all saved users`() {
        repository.save(User(id = UUID.randomUUID(), name = "Alice"))
        repository.save(User(id = UUID.randomUUID(), name = "Bob"))

        assertEquals(2, repository.findAll().size)
    }

    @Test
    fun `save updates existing user`() {
        val id = UUID.randomUUID()
        repository.save(User(id = id, name = "Original"))
        repository.save(User(id = id, name = "Updated"))

        val found = repository.findById(id)
        assertNotNull(found)
        assertEquals("Updated", found.name)
    }

    @Test
    fun `deleteById removes the user`() {
        val user = User(id = UUID.randomUUID(), name = "ToDelete")
        repository.save(user)
        repository.deleteById(user.id)

        assertNull(repository.findById(user.id))
    }
}
