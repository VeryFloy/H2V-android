package com.h2v.messenger.domain.model

data class User(
    val id: String,
    val nickname: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val avatar: String? = null,
    val bio: String? = null,
    val email: String? = null,
    val isOnline: Boolean = false,
    val lastOnline: String? = null
) {
    val displayName: String
        get() = when {
            !firstName.isNullOrBlank() && !lastName.isNullOrBlank() -> "$firstName $lastName"
            !firstName.isNullOrBlank() -> firstName
            else -> nickname
        }

    val initials: String
        get() {
            val name = displayName
            val parts = name.split(" ", "_").filter { it.isNotBlank() }
            return when {
                parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
                parts.isNotEmpty() -> parts[0].take(2).uppercase()
                else -> "??"
            }
        }
}
