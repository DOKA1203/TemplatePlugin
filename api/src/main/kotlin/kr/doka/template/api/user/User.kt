package kr.doka.template.api.user

import java.util.UUID

data class User(
    val id: UUID,
    val name: String,
)
