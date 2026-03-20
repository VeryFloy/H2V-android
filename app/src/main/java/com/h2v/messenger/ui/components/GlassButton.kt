package com.h2v.messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.h2v.messenger.ui.theme.H2VColors
import com.h2v.messenger.ui.theme.H2VTypography
import com.h2v.messenger.ui.theme.LiquidGlassTheme

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val alpha = if (enabled && !loading) 1f else 0.5f
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        H2VColors.AccentBlueSoft.copy(alpha = alpha),
                        H2VColors.AccentBlue.copy(alpha = alpha)
                    )
                )
            )
            .clickable(enabled = enabled && !loading) { onClick() }
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = H2VColors.TextOnAccent,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                style = H2VTypography.titleMedium,
                color = H2VColors.TextOnAccent
            )
        }
    }
}

@Preview
@Composable
private fun GlassButtonPreview() {
    LiquidGlassTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GlassButton("Send OTP", onClick = {})
            GlassButton("Loading...", onClick = {}, loading = true)
            GlassButton("Disabled", onClick = {}, enabled = false)
        }
    }
}
