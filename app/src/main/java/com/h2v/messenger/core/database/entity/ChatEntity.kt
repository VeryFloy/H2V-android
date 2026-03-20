package com.h2v.messenger.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val type: String,
    val name: String? = null,
    val avatar: String? = null,
    val unread: Int = 0,
    val lastMessageId: String? = null,
    val lastMessageText: String? = null,
    val lastMessageSenderName: String? = null,
    val lastMessageTime: String? = null,
    val lastMessageType: String? = null,
    val pinnedAt: Long? = null,
    val draftText: String? = null,
    val otherUserId: String? = null,
    val otherUserNickname: String? = null,
    val otherUserAvatar: String? = null,
    val otherUserIsOnline: Boolean = false,
    val updatedAt: String? = null
)
