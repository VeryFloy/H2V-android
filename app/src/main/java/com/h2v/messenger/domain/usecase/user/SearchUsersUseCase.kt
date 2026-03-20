package com.h2v.messenger.domain.usecase.user

import com.h2v.messenger.domain.model.User
import com.h2v.messenger.domain.repository.UserRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): Result<List<User>> =
        userRepository.searchUsers(query)
}
