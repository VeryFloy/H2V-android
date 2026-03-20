package com.h2v.messenger.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.h2v.messenger.domain.model.Message
import com.h2v.messenger.domain.model.MessageSender
import com.h2v.messenger.domain.model.MessageType
import com.h2v.messenger.ui.theme.H2VColors
import com.h2v.messenger.ui.theme.LiquidGlassTheme

@Composable
fun MessageBubble(
    message: Message,
    isOutgoing: Boolean,
    modifier: Modifier = Modifier
) {
    if (message.type == MessageType.SYSTEM) {
        SystemMessage(message)
        return
    }

    val bubbleColor = if (isOutgoing) H2VColors.BubbleOutgoing else H2VColors.BubbleIncoming
    val bubbleShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isOutgoing) 16.dp else 4.dp,
        bottomEnd = if (isOutgoing) 4.dp else 16.dp
    )

    Column(
        horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        if (message.isDeleted) {
            Box(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(bubbleColor.copy(alpha = 0.5f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Message deleted",
                    style = MaterialTheme.typography.bodyMedium,
                    color = H2VColors.TextTertiary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            return
        }

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column {
                if (message.replyTo != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(H2VColors.GlassWhite05, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = message.replyTo.senderName ?: "",
                                style = MaterialTheme.typography.labelMedium,
                                color = H2VColors.AccentBlue
                            )
                            Text(
                                text = message.replyTo.text ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = H2VColors.TextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                Text(
                    text = message.text ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = H2VColors.TextPrimary
                )

                Spacer(Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (message.isEdited) {
                        Text("edited ", style = MaterialTheme.typography.labelSmall, color = H2VColors.TextTertiary)
                    }
                    Text(
                        text = formatMessageTime(message.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = H2VColors.TextTertiary
                    )
                    if (isOutgoing && message.pending) {
                        Text(" ⏳", style = MaterialTheme.typography.labelSmall)
                    } else if (isOutgoing && message.readBy.isNotEmpty()) {
                        Text(" ✓✓", style = MaterialTheme.typography.labelSmall, color = H2VColors.AccentBlue)
                    } else if (isOutgoing && message.isDelivered) {
                        Text(" ✓✓", style = MaterialTheme.typography.labelSmall, color = H2VColors.TextTertiary)
                    } else if (isOutgoing) {
                        Text(" ✓", style = MaterialTheme.typography.labelSmall, color = H2VColors.TextTertiary)
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemMessage(message: Message) {
    Text(
        text = message.text ?: "",
        style = MaterialTheme.typography.bodySmall,
        color = H2VColors.TextTertiary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 32.dp)
    )
}

private fun formatMessageTime(isoTime: String): String {
    return try {
        val instant = java.time.Instant.parse(isoTime)
        val local = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault())
        "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
    } catch (_: Exception) { "" }
}

@Preview
@Composable
private fun BubblePreview() {
    LiquidGlassTheme {
        Column(Modifier.background(H2VColors.Background).padding(16.dp)) {
            MessageBubble(
                message = Message(id = "1", chatId = "c", sender = MessageSender("u1", "Alice"), text = "Hello!", createdAt = "2026-03-20T12:00:00Z"),
                isOutgoing = false
            )
            MessageBubble(
                message = Message(id = "2", chatId = "c", sender = MessageSender("u2", "Me"), text = "Hi there!", createdAt = "2026-03-20T12:01:00Z", isDelivered = true),
                isOutgoing = true
            )
        }
    }
}
