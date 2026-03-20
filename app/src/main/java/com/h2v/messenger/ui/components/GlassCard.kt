package com.h2v.messenger.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.h2v.messenger.ui.theme.LiquidGlassTheme
import com.h2v.messenger.ui.theme.glassCard

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .glassCard(cornerRadius),
        content = content
    )
}

@Preview
@Composable
private fun GlassCardPreview() {
    LiquidGlassTheme {
        GlassCard(modifier = Modifier.padding(16.dp)) {
            Box(Modifier.padding(24.dp))
        }
    }
}
