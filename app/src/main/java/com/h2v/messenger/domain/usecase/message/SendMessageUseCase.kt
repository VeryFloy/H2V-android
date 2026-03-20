package com.h2v.messenger.domain.usecase.message

import com.h2v.messenger.domain.model.Message
import com.h2v.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(chatId: String, text: String): Result<Message> =
        messageRepository.sendMessage(chatId, text)
}
