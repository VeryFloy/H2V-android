package com.h2v.messenger.domain.usecase.chat

import com.h2v.messenger.domain.model.Chat
import com.h2v.messenger.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(): Flow<List<Chat>> =
        chatRepository.getChats()
}
