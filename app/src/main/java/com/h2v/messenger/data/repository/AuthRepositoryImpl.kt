package com.h2v.messenger.data.repository

import com.h2v.messenger.core.database.dao.UserDao
import com.h2v.messenger.core.network.ApiService
import com.h2v.messenger.core.network.TokenManager
import com.h2v.messenger.core.network.WebSocketManager
import com.h2v.messenger.data.local.mapper.toEntity
import com.h2v.messenger.data.remote.dto.SendOtpRequest
import com.h2v.messenger.data.remote.dto.VerifyOtpRequest
import com.h2v.messenger.data.remote.mapper.toDomain
import com.h2v.messenger.domain.model.User
import com.h2v.messenger.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenManager: TokenManager,
    private val webSocketManager: WebSocketManager,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun sendOtp(email: String): Result<String> = runCatching {
        val response = api.sendOtp(SendOtpRequest(email))
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()?.data?.message ?: "OTP sent"
        } else {
            throw Exception(response.body()?.message ?: "Failed to send OTP")
        }
    }

    override suspend fun verifyOtp(email: String, code: String, nickname: String?): Result<User> = runCatching {
        val response = api.verifyOtp(VerifyOtpRequest(email, code, nickname))
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            tokenManager.saveToken(body.data.token)
            webSocketManager.connect()
            val user = body.data.user.toDomain()
            userDao.upsert(user.toEntity())
            user
        } else {
            throw Exception(body?.message ?: "Verification failed")
        }
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        try { api.logout() } catch (_: Exception) {}
        webSocketManager.disconnect()
        tokenManager.clearToken()
    }

    override fun isAuthenticated(): Flow<Boolean> =
        tokenManager.tokenFlow.map { it != null }
}
