package com.h2v.messenger.core.di

import com.h2v.messenger.data.repository.AuthRepositoryImpl
import com.h2v.messenger.data.repository.ChatRepositoryImpl
import com.h2v.messenger.data.repository.MessageRepositoryImpl
import com.h2v.messenger.data.repository.UserRepositoryImpl
import com.h2v.messenger.domain.repository.AuthRepository
import com.h2v.messenger.domain.repository.ChatRepository
import com.h2v.messenger.domain.repository.MessageRepository
import com.h2v.messenger.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
