package com.h2v.messenger.data.remote.mapper

import com.h2v.messenger.data.remote.dto.*
import com.h2v.messenger.domain.model.*

fun UserDto.toDomain() = User(
    id = id,
    nickname = nickname,
    firstName = firstName,
    lastName = lastName,
    avatar = avatar,
    bio = bio,
    email = email,
    isOnline = isOnline ?: false,
    lastOnline = lastOnline
)

fun ChatDto.toDomain(currentUserId: String) : Chat {
    val otherMember = members?.firstOrNull { it.userId != currentUserId }
    val otherUserDto = otherMember?.user
    return Chat(
        id = id,
        type = when (type) {
            "GROUP" -> ChatType.GROUP
            "SECRET" -> ChatType.SECRET
            "SELF" -> ChatType.SELF
            else -> ChatType.DIRECT
        },
        name = name,
        avatar = avatar,
        unread = unread ?: 0,
        lastMessage = lastMessage?.toPreview(),
        draft = draft?.text,
        otherUser = otherUserDto?.let {
            ChatUser(
                id = it.id,
                nickname = it.nickname,
                avatar = it.avatar,
                isOnline = it.isOnline ?: false
            )
        }
    )
}

fun MessageDto.toDomain() = Message(
    id = id,
    chatId = chatId ?: "",
    sender = sender?.let {
        MessageSender(id = it.id, nickname = it.nickname, avatar = it.avatar)
    } ?: MessageSender(id = "", nickname = "Unknown"),
    text = text,
    type = MessageType.fromString(type ?: "TEXT"),
    mediaUrl = mediaUrl,
    mediaName = mediaName,
    mediaSize = mediaSize,
    isEdited = isEdited ?: false,
    isDeleted = isDeleted ?: false,
    replyTo = replyTo?.let {
        ReplyTo(
            id = it.id,
            text = it.text,
            senderName = it.sender?.nickname
        )
    },
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDelivered = isDelivered ?: false,
    readBy = readBy ?: readReceipts?.map { it.userId } ?: emptyList()
)

fun MessageDto.toPreview() = MessagePreview(
    id = id,
    text = text,
    senderName = sender?.nickname,
    time = createdAt,
    type = type
)
