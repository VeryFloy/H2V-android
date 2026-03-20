package com.h2v.messenger.core.database.dao

import androidx.room.*
import com.h2v.messenger.core.database.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY CASE WHEN pinnedAt IS NOT NULL THEN 0 ELSE 1 END, pinnedAt DESC, updatedAt DESC")
    fun getAll(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :id")
    fun getById(id: String): Flow<ChatEntity?>

    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getByIdOnce(id: String): ChatEntity?

    @Upsert
    suspend fun upsert(chat: ChatEntity)

    @Upsert
    suspend fun upsertAll(chats: List<ChatEntity>)

    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM chats")
    suspend fun deleteAll()
}
