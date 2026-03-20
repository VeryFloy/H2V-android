package com.h2v.messenger.domain.model

data class Message(
    val id: String,
    val chatId: String,
    val sender: MessageSender,
    val text: String? = null,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val mediaName: String? = null,
    val mediaSize: Long? = null,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val replyTo: ReplyTo? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val isDelivered: Boolean = false,
    val pending: Boolean = false,
    val readBy: List<String> = emptyList()
)

data class MessageSender(
    val id: String,
    val nickname: String,
    val avatar: String? = null
)

data class ReplyTo(
    val id: String,
    val text: String?,
    val senderName: String?
)

enum class MessageType {
    TEXT, IMAGE, FILE, AUDIO, VIDEO, SYSTEM;

    companion object {
        fun fromString(value: String): MessageType =
            entries.firstOrNull { it.name == value } ?: TEXT
    }
}
