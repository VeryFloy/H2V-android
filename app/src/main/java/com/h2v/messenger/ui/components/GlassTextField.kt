package com.h2v.messenger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.h2v.messenger.ui.theme.H2VColors
import com.h2v.messenger.ui.theme.H2VTypography
import com.h2v.messenger.ui.theme.LiquidGlassTheme

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = H2VTypography.bodyLarge.copy(color = H2VColors.TextPrimary),
        cursorBrush = SolidColor(H2VColors.AccentBlue),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(H2VColors.InputBackground, RoundedCornerShape(14.dp))
                    .border(0.5.dp, H2VColors.InputBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = H2VTypography.bodyLarge,
                        color = H2VColors.TextTertiary
                    )
                }
                innerTextField()
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun GlassTextFieldPreview() {
    LiquidGlassTheme {
        Column(Modifier.padding(16.dp).background(H2VColors.Background)) {
            GlassTextField(value = "", onValueChange = {}, placeholder = "Email")
        }
    }
}
