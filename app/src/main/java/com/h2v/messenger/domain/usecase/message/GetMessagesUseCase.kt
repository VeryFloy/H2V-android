package com.h2v.messenger.domain.usecase.message

import com.h2v.messenger.domain.model.Message
import com.h2v.messenger.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(chatId: String): Flow<List<Message>> =
        messageRepository.getMessages(chatId)
}
