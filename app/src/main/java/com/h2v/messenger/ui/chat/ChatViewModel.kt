package com.h2v.messenger.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h2v.messenger.core.network.WebSocketManager
import com.h2v.messenger.domain.model.Chat
import com.h2v.messenger.domain.model.Message
import com.h2v.messenger.domain.repository.ChatRepository
import com.h2v.messenger.domain.usecase.message.GetMessagesUseCase
import com.h2v.messenger.domain.usecase.message.MarkReadUseCase
import com.h2v.messenger.domain.usecase.message.SendMessageUseCase
import com.h2v.messenger.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatState(
    val chat: Chat? = null,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,
    val typingUsers: List<String> = emptyList()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val markReadUseCase: MarkReadUseCase,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val webSocketManager: WebSocketManager
) : ViewModel() {

    val chatId: String = savedStateHandle.get<String>("chatId") ?: ""

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    init {
        loadChat()
        observeMessages()
        refreshMessages()
    }

    private fun loadChat() {
        viewModelScope.launch {
            chatRepository.getChatById(chatId)
                .onSuccess { chat -> _state.update { it.copy(chat = chat) } }
        }
    }

    private fun observeMessages() {
        viewModelScope.launch {
            getMessagesUseCase(chatId).collect { messages ->
                _state.update { it.copy(messages = messages) }
            }
        }
    }

    fun refreshMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            messageRepository.refreshMessages(chatId)
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onInputChange(text: String) {
        _state.update { it.copy(inputText = text) }
        if (text.isNotBlank()) {
            webSocketManager.send("typing:start", mapOf("chatId" to chatId))
        } else {
            webSocketManager.send("typing:stop", mapOf("chatId" to chatId))
        }
    }

    fun sendMessage() {
        val text = _state.value.inputText.trim()
        if (text.isBlank()) return

        _state.update { it.copy(inputText = "", isSending = true) }
        viewModelScope.launch {
            sendMessageUseCase(chatId, text)
            _state.update { it.copy(isSending = false) }
            webSocketManager.send("typing:stop", mapOf("chatId" to chatId))
        }
    }

    fun markMessageRead(messageId: String) {
        viewModelScope.launch {
            markReadUseCase(messageId)
        }
    }
}
