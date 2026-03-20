package com.h2v.messenger.domain.repository

import com.h2v.messenger.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun refreshMessages(chatId: String, cursor: String? = null): Result<String?>
    suspend fun sendMessage(chatId: String, text: String): Result<Message>
    suspend fun editMessage(id: String, text: String): Result<Message>
    suspend fun deleteMessage(id: String): Result<Unit>
    suspend fun markRead(id: String): Result<Unit>
}
