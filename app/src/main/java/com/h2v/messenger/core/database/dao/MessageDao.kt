package com.h2v.messenger.core.database.dao

import androidx.room.*
import com.h2v.messenger.core.database.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY createdAt ASC")
    fun getByChatId(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getById(id: String): MessageEntity?

    @Upsert
    suspend fun upsert(message: MessageEntity)

    @Upsert
    suspend fun upsertAll(messages: List<MessageEntity>)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteByChatId(chatId: String)

    @Query("UPDATE messages SET isDeleted = 1, text = null WHERE id = :id")
    suspend fun markDeleted(id: String)

    @Query("UPDATE messages SET text = :text, isEdited = 1 WHERE id = :id")
    suspend fun updateText(id: String, text: String)

    @Query("UPDATE messages SET pending = 0 WHERE id = :id")
    suspend fun clearPending(id: String)
}
