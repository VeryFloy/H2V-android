package com.h2v.messenger.domain.model

data class Chat(
    val id: String,
    val type: ChatType,
    val name: String? = null,
    val avatar: String? = null,
    val unread: Int = 0,
    val lastMessage: MessagePreview? = null,
    val pinnedAt: Long? = null,
    val draft: String? = null,
    val otherUser: ChatUser? = null
) {
    val displayName: String
        get() = when (type) {
            ChatType.DIRECT -> otherUser?.nickname ?: name ?: "Chat"
            ChatType.GROUP -> name ?: "Group"
            ChatType.SECRET -> otherUser?.nickname ?: name ?: "Secret Chat"
            ChatType.SELF -> "Saved Messages"
        }

    val displayAvatar: String?
        get() = when (type) {
            ChatType.DIRECT, ChatType.SECRET -> otherUser?.avatar ?: avatar
            else -> avatar
        }

    val displayInitials: String
        get() {
            val n = displayName
            val parts = n.split(" ", "_").filter { it.isNotBlank() }
            return when {
                parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
                parts.isNotEmpty() -> parts[0].take(2).uppercase()
                else -> "??"
            }
        }
}

enum class ChatType { DIRECT, GROUP, SECRET, SELF }

data class ChatUser(
    val id: String,
    val nickname: String,
    val avatar: String? = null,
    val isOnline: Boolean = false
)

data class MessagePreview(
    val id: String,
    val text: String?,
    val senderName: String?,
    val time: String?,
    val type: String? = "TEXT"
)
