package com.h2v.messenger.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.h2v.messenger.core.database.dao.ChatDao
import com.h2v.messenger.core.database.dao.MessageDao
import com.h2v.messenger.core.database.dao.UserDao
import com.h2v.messenger.core.database.entity.ChatEntity
import com.h2v.messenger.core.database.entity.MessageEntity
import com.h2v.messenger.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class H2VDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
