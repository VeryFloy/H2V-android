package com.h2v.messenger.data.repository

import com.h2v.messenger.core.database.dao.UserDao
import com.h2v.messenger.core.network.ApiService
import com.h2v.messenger.data.local.mapper.toDomain
import com.h2v.messenger.data.local.mapper.toEntity
import com.h2v.messenger.data.remote.dto.UpdateMeRequest
import com.h2v.messenger.data.remote.mapper.toDomain
import com.h2v.messenger.domain.model.User
import com.h2v.messenger.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val userDao: UserDao
) : UserRepository {

    private var currentUserId: String? = null

    override fun getCurrentUser(): Flow<User?> {
        return if (currentUserId != null) {
            userDao.getById(currentUserId!!).map { it?.toDomain() }
        } else {
            kotlinx.coroutines.flow.flow {
                val user = refreshCurrentUser().getOrNull()
                if (user != null) {
                    currentUserId = user.id
                    userDao.getById(user.id).collect { emit(it?.toDomain()) }
                } else {
                    emit(null)
                }
            }
        }
    }

    override suspend fun refreshCurrentUser(): Result<User> = runCatching {
        val response = api.getMe()
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            val user = body.data.toDomain()
            currentUserId = user.id
            userDao.upsert(user.toEntity())
            user
        } else {
            throw Exception(body?.message ?: "Failed to load profile")
        }
    }

    override suspend fun updateProfile(nickname: String?, bio: String?, avatar: String?): Result<User> = runCatching {
        val response = api.updateMe(UpdateMeRequest(nickname, bio, avatar))
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            val user = body.data.toDomain()
            userDao.upsert(user.toEntity())
            user
        } else {
            throw Exception(body?.message ?: "Failed to update profile")
        }
    }

    override suspend fun searchUsers(query: String): Result<List<User>> = runCatching {
        val response = api.searchUsers(query)
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            body.data.map { it.toDomain() }
        } else {
            throw Exception(body?.message ?: "Search failed")
        }
    }

    override suspend fun getUser(id: String): Result<User> = runCatching {
        val cached = userDao.getByIdOnce(id)
        if (cached != null) return@runCatching cached.toDomain()

        val response = api.getUser(id)
        val body = response.body()
        if (response.isSuccessful && body?.success == true && body.data != null) {
            val user = body.data.toDomain()
            userDao.upsert(user.toEntity())
            user
        } else {
            throw Exception(body?.message ?: "User not found")
        }
    }
}
