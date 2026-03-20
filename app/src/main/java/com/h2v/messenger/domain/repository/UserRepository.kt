package com.h2v.messenger.domain.repository

import com.h2v.messenger.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun refreshCurrentUser(): Result<User>
    suspend fun updateProfile(nickname: String?, bio: String?, avatar: String?): Result<User>
    suspend fun searchUsers(query: String): Result<List<User>>
    suspend fun getUser(id: String): Result<User>
}
