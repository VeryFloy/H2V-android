package com.h2v.messenger.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.h2v.messenger.ui.theme.H2VColors
import com.h2v.messenger.ui.theme.specularTopLine

data class BottomBarItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun GlassBottomBar(
    items: List<BottomBarItem>,
    selectedRoute: String,
    onItemClick: (BottomBarItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().specularTopLine())

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            H2VColors.SurfaceGlass.copy(alpha = 0.94f),
                            H2VColors.Background.copy(alpha = 0.98f)
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            items.forEach { item ->
                val isActive = item.route == selectedRoute
                GlassBottomBarItem(
                    icon = item.icon,
                    label = item.label,
                    isActive = isActive,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GlassBottomBarItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val iconScale by animateFloatAsState(
        targetValue = if (isActive) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "scale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.45f,
        animationSpec = tween(200),
        label = "iconAlpha"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (isActive) 0.9f else 0.35f,
        animationSpec = tween(200),
        label = "textAlpha"
    )
    val pillAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(250),
        label = "pill"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(vertical = 4.dp)
    ) {
        if (pillAlpha > 0f) {
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 40.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = pillAlpha * 0.12f),
                                H2VColors.AccentBlue.copy(alpha = pillAlpha * 0.06f),
                                Color.Transparent
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) H2VColors.AccentBlueLight.copy(alpha = iconAlpha)
                       else Color.White.copy(alpha = iconAlpha),
                modifier = Modifier.size(22.dp).scale(iconScale)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isActive) H2VColors.AccentBlueLight.copy(alpha = textAlpha)
                        else Color.White.copy(alpha = textAlpha),
                letterSpacing = 0.2.sp
            )
        }
    }
}
