package com.h2v.messenger.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.h2v.messenger.domain.repository.AuthRepository
import com.h2v.messenger.domain.repository.UserRepository
import com.h2v.messenger.ui.auth.AuthScreen
import com.h2v.messenger.ui.chat.ChatScreen
import com.h2v.messenger.ui.chatlist.ChatListScreen
import com.h2v.messenger.ui.components.BottomBarItem
import com.h2v.messenger.ui.components.GlassBottomBar
import com.h2v.messenger.ui.profile.ProfileScreen
import com.h2v.messenger.ui.theme.H2VColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

object Routes {
    const val AUTH = "auth"
    const val MAIN = "main"
    const val CHAT_LIST = "chatlist"
    const val PROFILE = "profile"
    const val CHAT = "chat/{chatId}"

    fun chat(chatId: String) = "chat/$chatId"
}

@HiltViewModel
class RootViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow<Boolean?>(null)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _currentUserId = MutableStateFlow("")
    val currentUserId = _currentUserId.asStateFlow()

    init {
        kotlinx.coroutines.MainScope().launch {
            val isAuth = authRepository.isAuthenticated().first()
            _isAuthenticated.value = isAuth
            if (isAuth) {
                userRepository.refreshCurrentUser().onSuccess { user ->
                    _currentUserId.value = user.id
                }
            }
        }
    }

    fun onAuthSuccess() {
        _isAuthenticated.value = true
        kotlinx.coroutines.MainScope().launch {
            userRepository.refreshCurrentUser().onSuccess { user ->
                _currentUserId.value = user.id
            }
        }
    }

    fun onLoggedOut() {
        _isAuthenticated.value = false
        _currentUserId.value = ""
    }
}

@Composable
fun AppNavigation(rootViewModel: RootViewModel = hiltViewModel()) {
    val isAuthenticated by rootViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val currentUserId by rootViewModel.currentUserId.collectAsStateWithLifecycle()

    if (isAuthenticated == null) {
        Box(Modifier.fillMaxSize().background(H2VColors.Background))
        return
    }

    val navController = rememberNavController()
    val startDestination = if (isAuthenticated == true) Routes.MAIN else Routes.AUTH

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(tween(300, easing = EaseOutCubic)) { it } + fadeIn(tween(200))
        },
        exitTransition = {
            slideOutHorizontally(tween(300, easing = EaseInCubic)) { -it / 4 } + fadeOut(tween(150))
        },
        popEnterTransition = {
            slideInHorizontally(tween(300, easing = EaseOutCubic)) { -it / 4 } + fadeIn(tween(200))
        },
        popExitTransition = {
            slideOutHorizontally(tween(300, easing = EaseInCubic)) { it } + fadeOut(tween(150))
        }
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = {
                    rootViewModel.onAuthSuccess()
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainTabScreen(
                currentUserId = currentUserId,
                onNavigateToChat = { chatId ->
                    navController.navigate(Routes.chat(chatId))
                },
                onLoggedOut = {
                    rootViewModel.onLoggedOut()
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) {
            ChatScreen(
                onBack = { navController.popBackStack() },
                currentUserId = currentUserId
            )
        }
    }
}

@Composable
private fun MainTabScreen(
    currentUserId: String,
    onNavigateToChat: (String) -> Unit,
    onLoggedOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(Routes.CHAT_LIST) }

    val bottomBarItems = remember {
        listOf(
            BottomBarItem(Icons.Filled.ChatBubble, "Chats", Routes.CHAT_LIST),
            BottomBarItem(Icons.Filled.Person, "Profile", Routes.PROFILE)
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(H2VColors.Background)) {
        when (selectedTab) {
            Routes.CHAT_LIST -> ChatListScreen(onChatClick = onNavigateToChat)
            Routes.PROFILE -> ProfileScreen(onLoggedOut = onLoggedOut)
        }

        GlassBottomBar(
            items = bottomBarItems,
            selectedRoute = selectedTab,
            onItemClick = { item -> selectedTab = item.route },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
