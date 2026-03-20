package com.h2v.messenger.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.h2v.messenger.ui.components.GlassButton
import com.h2v.messenger.ui.components.GlassTextField
import com.h2v.messenger.ui.theme.H2VColors

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onAuthSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(H2VColors.Background)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(H2VColors.AccentBlue.copy(alpha = 0.08f), H2VColors.Background),
                        center = Offset(size.width * 0.5f, size.height * 0.25f),
                        radius = size.width * 0.6f
                    ),
                    radius = size.width * 0.6f,
                    center = Offset(size.width * 0.5f, size.height * 0.25f)
                )
            }
            .statusBarsPadding()
            .imePadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 80.dp)
        ) {
            if (state.step != AuthStep.EMAIL) {
                IconButton(
                    onClick = { viewModel.goBack() },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = H2VColors.TextPrimary)
                }
                Spacer(Modifier.height(8.dp))
            }

            Text(
                text = "H2V",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = H2VColors.AccentBlue,
                letterSpacing = 4.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Messenger",
                fontSize = 16.sp,
                color = H2VColors.TextSecondary,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(48.dp))

            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                },
                label = "step"
            ) { step ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    when (step) {
                        AuthStep.EMAIL -> EmailStep(state, viewModel)
                        AuthStep.CODE -> CodeStep(state, viewModel)
                        AuthStep.NICKNAME -> NicknameStep(state, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmailStep(state: AuthState, viewModel: AuthViewModel) {
    val keyboard = LocalSoftwareKeyboardController.current

    Text("Sign in with email", style = MaterialTheme.typography.headlineMedium, color = H2VColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text("We'll send you a verification code", style = MaterialTheme.typography.bodyMedium, color = H2VColors.TextSecondary)
    Spacer(Modifier.height(32.dp))

    GlassTextField(
        value = state.email,
        onValueChange = viewModel::onEmailChange,
        placeholder = "Email",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = { keyboard?.hide(); viewModel.sendOtp() })
    )

    ErrorText(state.error)
    Spacer(Modifier.height(24.dp))

    GlassButton(text = "Send code", onClick = viewModel::sendOtp, loading = state.isLoading, enabled = state.email.isNotBlank())
}

@Composable
private fun CodeStep(state: AuthState, viewModel: AuthViewModel) {
    Text("Enter code", style = MaterialTheme.typography.headlineMedium, color = H2VColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text("Sent to ${state.email}", style = MaterialTheme.typography.bodyMedium, color = H2VColors.TextSecondary)
    Spacer(Modifier.height(32.dp))

    GlassTextField(
        value = state.code,
        onValueChange = viewModel::onCodeChange,
        placeholder = "000000",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { viewModel.submitCode() })
    )

    ErrorText(state.error)
    Spacer(Modifier.height(24.dp))

    GlassButton(text = "Verify", onClick = viewModel::submitCode, loading = state.isLoading, enabled = state.code.length == 6)

    Spacer(Modifier.height(16.dp))
    val cooldown = state.resendCooldown
    Text(
        text = if (cooldown > 0) "Resend in ${cooldown}s" else "Resend code",
        color = if (cooldown > 0) H2VColors.TextTertiary else H2VColors.AccentBlue,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.clickable(enabled = cooldown == 0) { viewModel.resendOtp() }
    )
}

@Composable
private fun NicknameStep(state: AuthState, viewModel: AuthViewModel) {
    Text("Choose a nickname", style = MaterialTheme.typography.headlineMedium, color = H2VColors.TextPrimary)
    Spacer(Modifier.height(8.dp))
    Text("This is how others will see you", style = MaterialTheme.typography.bodyMedium, color = H2VColors.TextSecondary)
    Spacer(Modifier.height(32.dp))

    GlassTextField(
        value = state.nickname,
        onValueChange = viewModel::onNicknameChange,
        placeholder = "Nickname",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { viewModel.submitNickname() })
    )

    ErrorText(state.error)
    Spacer(Modifier.height(24.dp))

    GlassButton(text = "Continue", onClick = viewModel::submitNickname, loading = state.isLoading, enabled = state.nickname.isNotBlank())
}

@Composable
private fun ErrorText(error: String?) {
    if (error != null) {
        Spacer(Modifier.height(12.dp))
        Text(text = error, color = H2VColors.Error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}
