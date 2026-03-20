package com.h2v.messenger.ui.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h2v.messenger.domain.model.Chat
import com.h2v.messenger.domain.model.User
import com.h2v.messenger.domain.usecase.chat.CreateDirectChatUseCase
import com.h2v.messenger.domain.usecase.chat.GetChatsUseCase
import com.h2v.messenger.domain.repository.ChatRepository
import com.h2v.messenger.domain.usecase.user.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatListState(
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<User> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getChatsUseCase: GetChatsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val createDirectChatUseCase: CreateDirectChatUseCase,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            getChatsUseCase().collect { chats ->
                _state.update { it.copy(chats = chats) }
            }
        }
        refreshChats()
    }

    fun refreshChats() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            chatRepository.refreshChats()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _state.update { it.copy(isSearching = true) }
            searchUsersUseCase(query)
                .onSuccess { users -> _state.update { it.copy(searchResults = users, isSearching = false) } }
                .onFailure { _state.update { it.copy(isSearching = false) } }
        }
    }

    fun createChat(userId: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            createDirectChatUseCase(userId)
                .onSuccess { chat ->
                    _state.update { it.copy(searchQuery = "", searchResults = emptyList()) }
                    onSuccess(chat.id)
                }
                .onFailure { e -> _state.update { it.copy(error = e.message) } }
        }
    }

    fun clearSearch() {
        _state.update { it.copy(searchQuery = "", searchResults = emptyList()) }
    }
}
