package com.h2v.messenger.domain.usecase.user

import com.h2v.messenger.domain.model.User
import com.h2v.messenger.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String? = null, bio: String? = null, avatar: String? = null): Result<User> =
        userRepository.updateProfile(nickname, bio, avatar)
}
