package com.h2v.messenger.data.local.mapper

import com.h2v.messenger.core.database.entity.*
import com.h2v.messenger.domain.model.*

fun UserEntity.toDomain() = User(
    id = id,
    nickname = nickname,
    firstName = firstName,
    lastName = lastName,
    avatar = avatar,
    bio = bio,
    email = email,
    isOnline = isOnline,
    lastOnline = lastOnline
)

fun User.toEntity() = UserEntity(
    id = id,
    nickname = nickname,
    firstName = firstName,
    lastName = lastName,
    avatar = avatar,
    bio = bio,
    email = email,
    isOnline = isOnline,
    lastOnline = lastOnline
)

fun ChatEntity.toDomain() = Chat(
    id = id,
    type = when (type) {
        "GROUP" -> ChatType.GROUP
        "SECRET" -> ChatType.SECRET
        "SELF" -> ChatType.SELF
        else -> ChatType.DIRECT
    },
    name = name,
    avatar = avatar,
    unread = unread,
    lastMessage = lastMessageId?.let {
        MessagePreview(
            id = it,
            text = lastMessageText,
            senderName = lastMessageSenderName,
            time = lastMessageTime,
            type = lastMessageType
        )
    },
    pinnedAt = pinnedAt,
    draft = draftText,
    otherUser = otherUserId?.let {
        ChatUser(
            id = it,
            nickname = otherUserNickname ?: "",
            avatar = otherUserAvatar,
            isOnline = otherUserIsOnline
        )
    }
)

fun Chat.toEntity() = ChatEntity(
    id = id,
    type = type.name,
    name = name,
    avatar = avatar,
    unread = unread,
    lastMessageId = lastMessage?.id,
    lastMessageText = lastMessage?.text,
    lastMessageSenderName = lastMessage?.senderName,
    lastMessageTime = lastMessage?.time,
    lastMessageType = lastMessage?.type,
    pinnedAt = pinnedAt,
    draftText = draft,
    otherUserId = otherUser?.id,
    otherUserNickname = otherUser?.nickname,
    otherUserAvatar = otherUser?.avatar,
    otherUserIsOnline = otherUser?.isOnline ?: false,
    updatedAt = lastMessage?.time
)

fun MessageEntity.toDomain() = Message(
    id = id,
    chatId = chatId,
    sender = MessageSender(
        id = senderId,
        nickname = senderNickname,
        avatar = senderAvatar
    ),
    text = text,
    type = MessageType.fromString(type),
    mediaUrl = mediaUrl,
    mediaName = mediaName,
    mediaSize = mediaSize,
    isEdited = isEdited,
    isDeleted = isDeleted,
    replyTo = replyToId?.let {
        ReplyTo(id = it, text = replyToText, senderName = replyToSenderName)
    },
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDelivered = isDelivered,
    pending = pending,
    readBy = readByJson?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
)

fun Message.toEntity() = MessageEntity(
    id = id,
    chatId = chatId,
    senderId = sender.id,
    senderNickname = sender.nickname,
    senderAvatar = sender.avatar,
    text = text,
    type = type.name,
    mediaUrl = mediaUrl,
    mediaName = mediaName,
    mediaSize = mediaSize,
    isEdited = isEdited,
    isDeleted = isDeleted,
    replyToId = replyTo?.id,
    replyToText = replyTo?.text,
    replyToSenderName = replyTo?.senderName,
    createdAt = createdAt,
    updatedAt = updatedAt,
    isDelivered = isDelivered,
    pending = pending,
    readByJson = readBy.joinToString(",")
)
