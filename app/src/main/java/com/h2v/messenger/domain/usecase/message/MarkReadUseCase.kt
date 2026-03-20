package com.h2v.messenger.domain.usecase.message

import com.h2v.messenger.domain.repository.MessageRepository
import javax.inject.Inject

class MarkReadUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(messageId: String): Result<Unit> =
        messageRepository.markRead(messageId)
}
