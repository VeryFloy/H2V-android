package com.h2v.messenger.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h2v.messenger.domain.model.User
import com.h2v.messenger.domain.usecase.auth.LogoutUseCase
import com.h2v.messenger.domain.usecase.user.GetProfileUseCase
import com.h2v.messenger.domain.usecase.user.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val user: User? = null,
    val editNickname: String = "",
    val editBio: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getProfileUseCase().collect { user ->
                _state.update { it.copy(user = user) }
                if (!_state.value.isEditing && user != null) {
                    _state.update { it.copy(editNickname = user.nickname, editBio = user.bio ?: "") }
                }
            }
        }
    }

    fun startEditing() {
        val user = _state.value.user ?: return
        _state.update { it.copy(isEditing = true, editNickname = user.nickname, editBio = user.bio ?: "") }
    }

    fun cancelEditing() {
        _state.update { it.copy(isEditing = false, error = null) }
    }

    fun onNicknameChange(value: String) {
        _state.update { it.copy(editNickname = value, error = null) }
    }

    fun onBioChange(value: String) {
        _state.update { it.copy(editBio = value, error = null) }
    }

    fun saveProfile() {
        val s = _state.value
        if (s.editNickname.isBlank()) {
            _state.update { it.copy(error = "Nickname cannot be empty") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            updateProfileUseCase(
                nickname = s.editNickname.takeIf { it != s.user?.nickname },
                bio = s.editBio.takeIf { it != (s.user?.bio ?: "") }
            )
                .onSuccess { _state.update { it.copy(isSaving = false, isEditing = false) } }
                .onFailure { e -> _state.update { it.copy(isSaving = false, error = e.message) } }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _state.update { it.copy(loggedOut = true) }
        }
    }
}
