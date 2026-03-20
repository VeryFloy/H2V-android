package com.h2v.messenger.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.h2v.messenger.ui.theme.H2VColors
import com.h2v.messenger.ui.theme.H2VTypography
import com.h2v.messenger.ui.theme.LiquidGlassTheme
import com.h2v.messenger.ui.theme.specularTopLine

@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .specularTopLine()
            .background(H2VColors.SurfaceGlass)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = H2VTypography.bodyLarge.copy(color = H2VColors.TextPrimary),
            cursorBrush = SolidColor(H2VColors.AccentBlue),
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { if (value.isNotBlank()) onSend() }),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp)
                        .background(H2VColors.InputBackground, RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    if (value.isEmpty()) {
                        Text("Message", style = H2VTypography.bodyLarge, color = H2VColors.TextTertiary)
                    }
                    innerTextField()
                }
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(8.dp))

        IconButton(
            onClick = onSend,
            enabled = value.isNotBlank() && enabled,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (value.isNotBlank()) H2VColors.AccentBlue else H2VColors.SurfaceElevated,
                    CircleShape
                )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (value.isNotBlank()) H2VColors.TextOnAccent else H2VColors.TextTertiary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ChatInputPreview() {
    LiquidGlassTheme {
        ChatInput(value = "Hello", onValueChange = {}, onSend = {})
    }
}
