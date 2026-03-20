package com.h2v.messenger.core.di

import android.content.Context
import androidx.room.Room
import com.h2v.messenger.core.database.H2VDatabase
import com.h2v.messenger.core.database.dao.ChatDao
import com.h2v.messenger.core.database.dao.MessageDao
import com.h2v.messenger.core.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): H2VDatabase {
        return Room.databaseBuilder(
            context,
            H2VDatabase::class.java,
            "h2v_messenger.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(db: H2VDatabase): UserDao = db.userDao()

    @Provides
    fun provideChatDao(db: H2VDatabase): ChatDao = db.chatDao()

    @Provides
    fun provideMessageDao(db: H2VDatabase): MessageDao = db.messageDao()
}
