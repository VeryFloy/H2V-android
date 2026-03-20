package com.h2v.messenger.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h2v.messenger.domain.usecase.auth.SendOtpUseCase
import com.h2v.messenger.domain.usecase.auth.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val step: AuthStep = AuthStep.EMAIL,
    val email: String = "",
    val code: String = "",
    val nickname: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val resendCooldown: Int = 0,
    val isNewUser: Boolean = false,
    val isSuccess: Boolean = false
)

enum class AuthStep { EMAIL, CODE, NICKNAME }

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private var cooldownJob: Job? = null

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value.trim(), error = null) }
    }

    fun onCodeChange(value: String) {
        if (value.length <= 6) {
            _state.update { it.copy(code = value.filter { c -> c.isDigit() }, error = null) }
            if (value.length == 6) submitCode()
        }
    }

    fun onNicknameChange(value: String) {
        _state.update { it.copy(nickname = value.trim(), error = null) }
    }

    fun sendOtp() {
        val email = _state.value.email
        if (email.isBlank() || !email.contains("@")) {
            _state.update { it.copy(error = "Enter a valid email") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            sendOtpUseCase(email)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, step = AuthStep.CODE) }
                    startCooldown()
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun submitCode() {
        val s = _state.value
        if (s.code.length != 6) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            verifyOtpUseCase(s.email, s.code)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    val msg = e.message ?: "Verification failed"
                    if (msg.contains("nickname", ignoreCase = true) || msg.contains("new user", ignoreCase = true)) {
                        _state.update { it.copy(isLoading = false, step = AuthStep.NICKNAME, isNewUser = true, error = null) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = msg) }
                    }
                }
        }
    }

    fun submitNickname() {
        val s = _state.value
        if (s.nickname.isBlank()) {
            _state.update { it.copy(error = "Enter a nickname") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            verifyOtpUseCase(s.email, s.code, s.nickname)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun resendOtp() {
        if (_state.value.resendCooldown > 0) return
        viewModelScope.launch {
            sendOtpUseCase(_state.value.email)
                .onSuccess { startCooldown() }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun goBack() {
        val step = _state.value.step
        when (step) {
            AuthStep.CODE -> _state.update { it.copy(step = AuthStep.EMAIL, code = "", error = null) }
            AuthStep.NICKNAME -> _state.update { it.copy(step = AuthStep.CODE, error = null) }
            else -> {}
        }
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        _state.update { it.copy(resendCooldown = 60) }
        cooldownJob = viewModelScope.launch {
            for (i in 59 downTo 0) {
                delay(1000)
                _state.update { it.copy(resendCooldown = i) }
            }
        }
    }
}
