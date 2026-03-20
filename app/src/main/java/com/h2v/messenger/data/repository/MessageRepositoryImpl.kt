package com.h2v.messenger.data.repository

import com.h2v.messenger.core.database.dao.MessageDao
import com.h2v.messenger.core.network.ApiService
import com.h2v.messenger.core.network.WebSocketManager
import com.h2v.messenger.data.local.mapper.toDomain
import com.h2v.messenger.data.local.mapper.toEntity
import com.h2v.messenger.data.remote.dto.EditMessageRequest
import com.h2v.messenger.data.remote.mapper.toDomain
import com.h2v.messenger.domain.model.Message
import com.h2v.messenger.domain.model.MessageSender
import com.h2v.messenger.domain.model.MessageType
import com.h2v.messenger.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val messageDao: MessageDao,
    private val webSocketManager: WebSocketManager
) : MessageRepository {

    override fun getMessages(chatId: String): Flow<List<Message>> =
        messageDao.getByChatId(chatId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshMessages(chatId: String, cursor: String?): Result<String?> = runCatching {
        val response = api.getMessages(chatId, cursor = cursor)
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            val messages = body.data.messages.map { it.toDomain() }
            messageDao.upsertAll(messages.map { it.toEntity() })
            body.data.nextCursor
        } else {
            throw Exception(body?.message ?: "Failed to load messages")
        }
    }

    override suspend fun sendMessage(chatId: String, text: String): Result<Message> = runCatching {
        val tempId = "temp_${UUID.randomUUID()}"
        val pendingMessage = Message(
            id = tempId,
            chatId = chatId,
            sender = MessageSender(id = "", nickname = ""),
            text = text,
            type = MessageType.TEXT,
            createdAt = java.time.Instant.now().toString(),
            pending = true
        )
        messageDao.upsert(pendingMessage.toEntity())

        webSocketManager.send("message:send", mapOf(
            "chatId" to chatId,
            "text" to text,
            "type" to "TEXT"
        ))

        pendingMessage
    }

    override suspend fun editMessage(id: String, text: String): Result<Message> = runCatching {
        val response = api.editMessage(id, EditMessageRequest(text))
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            val message = body.data.toDomain()
            messageDao.updateText(id, text)
            message
        } else {
            throw Exception(body?.message ?: "Failed to edit message")
        }
    }

    override suspend fun deleteMessage(id: String): Result<Unit> = runCatching {
        val response = api.deleteMessage(id)
        if (response.isSuccessful) {
            messageDao.markDeleted(id)
        } else {
            throw Exception(response.body()?.message ?: "Failed to delete message")
        }
    }

    override suspend fun markRead(id: String): Result<Unit> = runCatching {
        api.markRead(id)
        webSocketManager.send("message:read", mapOf("messageId" to id))
    }
}
