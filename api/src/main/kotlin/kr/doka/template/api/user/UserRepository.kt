package kr.doka.template.api.user

import java.util.UUID

interface UserRepository {
    fun findById(id: UUID): User?

    fun findAll(): List<User>

    fun save(user: User): User

    fun deleteById(id: UUID)
}
