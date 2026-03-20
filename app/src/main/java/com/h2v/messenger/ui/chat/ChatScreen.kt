package com.h2v.messenger.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.h2v.messenger.ui.components.AvatarImage
import com.h2v.messenger.ui.theme.H2VColors
import com.h2v.messenger.ui.theme.specularTopLine

@Composable
fun ChatScreen(
    onBack: () -> Unit,
    currentUserId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(H2VColors.Background)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .specularTopLine()
                .background(H2VColors.SurfaceGlass)
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = H2VColors.TextPrimary)
            }
            state.chat?.let { chat ->
                AvatarImage(
                    imageUrl = chat.displayAvatar,
                    initials = chat.displayInitials,
                    size = 40.dp
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        chat.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = H2VColors.TextPrimary
                    )
                    if (chat.otherUser?.isOnline == true) {
                        Text("online", style = MaterialTheme.typography.bodySmall, color = H2VColors.OnlineDot)
                    }
                    if (state.typingUsers.isNotEmpty()) {
                        Text("typing...", style = MaterialTheme.typography.bodySmall, color = H2VColors.AccentBlue)
                    }
                }
            }
        }

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp),
            reverseLayout = false
        ) {
            items(state.messages, key = { it.id }) { message ->
                MessageBubble(
                    message = message,
                    isOutgoing = message.sender.id == currentUserId
                )

                LaunchedEffect(message.id) {
                    if (message.sender.id != currentUserId && message.readBy.isEmpty()) {
                        viewModel.markMessageRead(message.id)
                    }
                }
            }
        }

        // Input
        ChatInput(
            value = state.inputText,
            onValueChange = viewModel::onInputChange,
            onSend = viewModel::sendMessage,
            enabled = !state.isSending
        )
    }
}
