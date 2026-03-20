package com.h2v.messenger.domain.usecase.user

import com.h2v.messenger.domain.model.User
import com.h2v.messenger.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> =
        userRepository.getCurrentUser()
}
