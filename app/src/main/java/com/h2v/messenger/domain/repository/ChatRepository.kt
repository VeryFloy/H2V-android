package com.h2v.messenger.domain.repository

import com.h2v.messenger.domain.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    suspend fun refreshChats(cursor: String? = null): Result<String?>
    suspend fun getChatById(id: String): Result<Chat>
    suspend fun createDirectChat(targetUserId: String): Result<Chat>
}
