package com.h2v.messenger.domain.repository

import com.h2v.messenger.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun sendOtp(email: String): Result<String>
    suspend fun verifyOtp(email: String, code: String, nickname: String? = null): Result<User>
    suspend fun logout(): Result<Unit>
    fun isAuthenticated(): Flow<Boolean>
}
