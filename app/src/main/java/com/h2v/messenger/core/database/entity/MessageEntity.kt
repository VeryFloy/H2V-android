package com.h2v.messenger.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderNickname: String,
    val senderAvatar: String? = null,
    val text: String? = null,
    val type: String = "TEXT",
    val mediaUrl: String? = null,
    val mediaName: String? = null,
    val mediaSize: Long? = null,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val replyToId: String? = null,
    val replyToText: String? = null,
    val replyToSenderName: String? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val isDelivered: Boolean = false,
    val pending: Boolean = false,
    val readByJson: String? = null
)
