package com.h2v.messenger.domain.usecase.auth

import com.h2v.messenger.domain.repository.AuthRepository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<String> =
        authRepository.sendOtp(email)
}
