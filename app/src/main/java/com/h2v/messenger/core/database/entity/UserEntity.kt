package com.h2v.messenger.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val nickname: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val avatar: String? = null,
    val bio: String? = null,
    val email: String? = null,
    val isOnline: Boolean = false,
    val lastOnline: String? = null
)
