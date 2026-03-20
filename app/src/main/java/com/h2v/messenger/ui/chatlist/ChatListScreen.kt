package com.h2v.messenger.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.h2v.messenger.domain.model.Chat
import com.h2v.messenger.domain.model.User
import com.h2v.messenger.ui.components.AvatarImage
import com.h2v.messenger.ui.components.GlassTextField
import com.h2v.messenger.ui.theme.H2VColors
import com.h2v.messenger.ui.theme.glassCard

@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit,
    viewModel: ChatListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(H2VColors.Background)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                "Chats",
                style = MaterialTheme.typography.headlineMedium,
                color = H2VColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        // Search bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp)
        ) {
            GlassTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchChange,
                placeholder = "Search users..."
            )
        }

        if (state.searchQuery.isNotBlank() && state.searchResults.isNotEmpty()) {
            // Search results
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(state.searchResults, key = { it.id }) { user ->
                    SearchResultItem(
                        user = user,
                        onClick = { viewModel.createChat(user.id) { chatId -> onChatClick(chatId) } }
                    )
                }
            }
        } else {
            // Chat list
            if (state.chats.isEmpty() && !state.isRefreshing) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No chats yet", color = H2VColors.TextTertiary, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    items(state.chats, key = { it.id }) { chat ->
                        ChatListItem(
                            chat = chat,
                            onClick = { onChatClick(chat.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatListItem(chat: Chat, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Box {
            AvatarImage(
                imageUrl = chat.displayAvatar,
                initials = chat.displayInitials,
                size = 52.dp
            )
            if (chat.otherUser?.isOnline == true) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(H2VColors.Background)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(H2VColors.OnlineDot)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = H2VColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                chat.lastMessage?.time?.let { time ->
                    Text(
                        text = formatTime(time),
                        style = MaterialTheme.typography.labelSmall,
                        color = H2VColors.TextTertiary
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = chat.lastMessage?.text ?: chat.draft?.let { "Draft: $it" } ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (chat.draft != null) H2VColors.Error else H2VColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (chat.unread > 0) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(H2VColors.UnreadBadge)
                    ) {
                        Text(
                            text = if (chat.unread > 99) "99+" else chat.unread.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = H2VColors.TextOnAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(user: User, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        AvatarImage(imageUrl = user.avatar, initials = user.initials, size = 44.dp)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(user.displayName, style = MaterialTheme.typography.titleMedium, color = H2VColors.TextPrimary)
            Text("@${user.nickname}", style = MaterialTheme.typography.bodySmall, color = H2VColors.TextSecondary)
        }
    }
}

private fun formatTime(isoTime: String): String {
    return try {
        val instant = java.time.Instant.parse(isoTime)
        val local = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
        val now = java.time.LocalDate.now()
        if (local.toLocalDate() == now) {
            "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
        } else {
            "${local.dayOfMonth}.${local.monthValue.toString().padStart(2, '0')}"
        }
    } catch (_: Exception) {
        ""
    }
}
