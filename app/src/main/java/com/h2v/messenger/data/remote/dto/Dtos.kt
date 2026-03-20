package com.h2v.messenger.data.remote.dto

import com.google.gson.annotations.SerializedName

// === Auth ===

data class SendOtpRequest(val email: String)

data class SendOtpResponse(val message: String? = null)

data class VerifyOtpRequest(
    val email: String,
    val code: String,
    val nickname: String? = null
)

data class VerifyOtpResponse(
    val user: UserDto,
    val token: String
)

// === User ===

data class UserDto(
    val id: String,
    val nickname: String,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    val avatar: String? = null,
    val bio: String? = null,
    val email: String? = null,
    val isOnline: Boolean? = null,
    val lastOnline: String? = null
)

data class UpdateMeRequest(
    val nickname: String? = null,
    val bio: String? = null,
    val avatar: String? = null
)

// === Chat ===

data class ChatDto(
    val id: String,
    val type: String,
    val name: String? = null,
    val avatar: String? = null,
    @SerializedName("pinnedMessageId") val pinnedMessageId: String? = null,
    val createdAt: String? = null,
    val members: List<ChatMemberDto>? = null,
    val lastMessage: MessageDto? = null,
    val unread: Int? = null,
    val draft: ChatDraftDto? = null
)

data class ChatMemberDto(
    val id: String,
    val userId: String,
    val chatId: String,
    val role: String,
    val joinedAt: String? = null,
    val pinnedAt: String? = null,
    val user: UserDto? = null
)

data class ChatDraftDto(
    val text: String? = null,
    @SerializedName("replyToId") val replyToId: String? = null,
    val updatedAt: String? = null
)

data class ChatsResponse(
    val chats: List<ChatDto>,
    val nextCursor: String? = null
)

data class CreateDirectChatRequest(
    val targetUserId: String
)

// === Message ===

data class MessageDto(
    val id: String,
    val chatId: String? = null,
    val text: String? = null,
    val ciphertext: String? = null,
    val signalType: Int? = null,
    val type: String? = null,
    val mediaUrl: String? = null,
    val mediaName: String? = null,
    val mediaSize: Long? = null,
    @SerializedName("replyToId") val replyToId: String? = null,
    val isEdited: Boolean? = null,
    val isDeleted: Boolean? = null,
    val createdAt: String,
    val updatedAt: String? = null,
    val sender: MessageSenderDto? = null,
    val readReceipts: List<ReadReceiptDto>? = null,
    val reactions: List<ReactionDto>? = null,
    val replyTo: ReplyToDto? = null,
    val isDelivered: Boolean? = null,
    val readBy: List<String>? = null
)

data class MessageSenderDto(
    val id: String,
    val nickname: String,
    val avatar: String? = null,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null
)

data class ReadReceiptDto(
    val userId: String,
    val readAt: String
)

data class ReactionDto(
    val id: String,
    val userId: String,
    val emoji: String
)

data class ReplyToDto(
    val id: String,
    val text: String? = null,
    val isDeleted: Boolean? = null,
    val sender: MessageSenderDto? = null
)

data class MessagesResponse(
    val messages: List<MessageDto>,
    val nextCursor: String? = null
)

data class EditMessageRequest(val text: String)
