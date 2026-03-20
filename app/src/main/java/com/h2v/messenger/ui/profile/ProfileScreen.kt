package com.h2v.messenger.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.h2v.messenger.ui.components.AvatarImage
import com.h2v.messenger.ui.components.GlassButton
import com.h2v.messenger.ui.components.GlassCard
import com.h2v.messenger.ui.components.GlassTextField
import com.h2v.messenger.ui.theme.H2VColors

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLoggedOut()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(H2VColors.Background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Profile", style = MaterialTheme.typography.headlineMedium, color = H2VColors.TextPrimary, modifier = Modifier.weight(1f))
            if (!state.isEditing) {
                IconButton(onClick = viewModel::startEditing) {
                    Icon(Icons.Default.Edit, "Edit", tint = H2VColors.AccentBlue)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        val user = state.user
        AvatarImage(
            imageUrl = user?.avatar,
            initials = user?.initials ?: "??",
            size = 96.dp
        )

        Spacer(Modifier.height(16.dp))

        if (state.isEditing) {
            GlassTextField(
                value = state.editNickname,
                onValueChange = viewModel::onNicknameChange,
                placeholder = "Nickname"
            )
            Spacer(Modifier.height(12.dp))
            GlassTextField(
                value = state.editBio,
                onValueChange = viewModel::onBioChange,
                placeholder = "Bio",
                singleLine = false
            )

            if (state.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(state.error!!, color = H2VColors.Error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = viewModel::cancelEditing,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = H2VColors.TextSecondary),
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }
                GlassButton(
                    text = "Save",
                    onClick = viewModel::saveProfile,
                    loading = state.isSaving,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Text(
                text = user?.displayName ?: "",
                style = MaterialTheme.typography.headlineMedium,
                color = H2VColors.TextPrimary
            )
            if (!user?.bio.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = user!!.bio!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = H2VColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
            if (!user?.email.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(user!!.email!!, style = MaterialTheme.typography.bodySmall, color = H2VColors.TextTertiary)
            }
        }

        Spacer(Modifier.weight(1f))

        TextButton(
            onClick = viewModel::logout,
            colors = ButtonDefaults.textButtonColors(contentColor = H2VColors.Error),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Log out")
        }
    }
}
