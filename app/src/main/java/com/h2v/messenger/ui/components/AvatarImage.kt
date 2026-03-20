package com.h2v.messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.h2v.messenger.BuildConfig
import com.h2v.messenger.ui.theme.H2VColors
import com.h2v.messenger.ui.theme.LiquidGlassTheme

@Composable
fun AvatarImage(
    imageUrl: String?,
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    val fullUrl = when {
        imageUrl == null -> null
        imageUrl.startsWith("http") -> imageUrl
        else -> "${BuildConfig.BASE_URL}$imageUrl"
    }

    if (fullUrl != null) {
        AsyncImage(
            model = fullUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(H2VColors.SurfaceElevated, CircleShape)
        )
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(H2VColors.AccentBlueDim, CircleShape)
        ) {
            Text(
                text = initials,
                fontSize = (size.value * 0.36f).sp,
                fontWeight = FontWeight.SemiBold,
                color = H2VColors.AccentBlueLight
            )
        }
    }
}

@Preview
@Composable
private fun AvatarPreview() {
    LiquidGlassTheme {
        AvatarImage(imageUrl = null, initials = "H2")
    }
}
