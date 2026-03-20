package com.h2v.messenger.domain.usecase.chat

import com.h2v.messenger.domain.model.Chat
import com.h2v.messenger.domain.repository.ChatRepository
import javax.inject.Inject

class CreateDirectChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(targetUserId: String): Result<Chat> =
        chatRepository.createDirectChat(targetUserId)
}
