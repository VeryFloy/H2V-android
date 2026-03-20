package com.h2v.messenger.domain.usecase.auth

import com.h2v.messenger.domain.model.User
import com.h2v.messenger.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, code: String, nickname: String? = null): Result<User> =
        authRepository.verifyOtp(email, code, nickname)
}
