package kr.doka.template.core.user

import kr.doka.template.api.user.User
import kr.doka.template.api.user.UserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserRepositoryImpl : UserRepository {
    override fun findById(id: UUID): User? =
        transaction {
            UserEntity.findById(id)?.toUser()
        }

    override fun findAll(): List<User> =
        transaction {
            UserEntity.all().map { it.toUser() }
        }

    override fun save(user: User): User =
        transaction {
            UserEntity
                .findById(user.id)
                ?.also { entity ->
                    entity.name = user.name
                }?.toUser() ?: UserEntity
                .new(user.id) {
                    name = user.name
                }.toUser()
        }

    override fun deleteById(id: UUID) {
        transaction {
            UserEntity.findById(id)?.delete()
        }
    }
}
