package com.h2v.messenger.data.repository

import com.h2v.messenger.core.database.dao.ChatDao
import com.h2v.messenger.core.database.dao.UserDao
import com.h2v.messenger.core.network.ApiService
import com.h2v.messenger.core.network.TokenManager
import com.h2v.messenger.data.local.mapper.toDomain
import com.h2v.messenger.data.local.mapper.toEntity
import com.h2v.messenger.data.remote.dto.CreateDirectChatRequest
import com.h2v.messenger.data.remote.mapper.toDomain
import com.h2v.messenger.domain.model.Chat
import com.h2v.messenger.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val chatDao: ChatDao,
    private val userDao: UserDao,
    private val tokenManager: TokenManager
) : ChatRepository {

    private var currentUserId: String? = null

    private suspend fun getCurrentUserId(): String {
        if (currentUserId != null) return currentUserId!!
        val response = api.getMe()
        val user = response.body()?.data
        currentUserId = user?.id ?: ""
        return currentUserId!!
    }

    override fun getChats(): Flow<List<Chat>> =
        chatDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun refreshChats(cursor: String?): Result<String?> = runCatching {
        val userId = getCurrentUserId()
        val response = api.getChats(cursor = cursor)
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            val chats = body.data.chats.map { it.toDomain(userId) }
            chatDao.upsertAll(chats.map { it.toEntity() })
            body.data.nextCursor
        } else {
            throw Exception(body?.message ?: "Failed to load chats")
        }
    }

    override suspend fun getChatById(id: String): Result<Chat> = runCatching {
        val userId = getCurrentUserId()
        val response = api.getChat(id)
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            val chat = body.data.toDomain(userId)
            chatDao.upsert(chat.toEntity())
            chat
        } else {
            chatDao.getByIdOnce(id)?.toDomain()
                ?: throw Exception(body?.message ?: "Chat not found")
        }
    }

    override suspend fun createDirectChat(targetUserId: String): Result<Chat> = runCatching {
        val userId = getCurrentUserId()
        val response = api.createDirectChat(CreateDirectChatRequest(targetUserId))
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            val chat = body.data.toDomain(userId)
            chatDao.upsert(chat.toEntity())
            chat
        } else {
            throw Exception(body?.message ?: "Failed to create chat")
        }
    }
}
