package com.shub39.rush.app

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.billing.PaywallPage
import com.shub39.rush.core.data.listener.NotificationListener
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.lyrics.LyricsGraph
import com.shub39.rush.onboarding.Onboarding
import com.shub39.rush.saved.SavedPage
import com.shub39.rush.search_sheet.SearchSheet
import com.shub39.rush.setting.SettingsGraph
import com.shub39.rush.setting.SettingsPageAction
import com.shub39.rush.share.SharePage
import com.shub39.rush.viewmodels.LyricsVM
import com.shub39.rush.viewmodels.SavedVM
import com.shub39.rush.viewmodels.SearchSheetVM
import com.shub39.rush.viewmodels.SettingsVM
import com.shub39.rush.viewmodels.ShareVM
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
sealed interface Route {
    @Serializable
    data object SavedPage: Route

    @Serializable
    data object LyricsGraph: Route

    @Serializable
    data object SettingsGraph: Route

    @Serializable
    data object Onboarding: Route

    @Serializable
    data object SharePage: Route
}

@Composable
fun RushApp() {
    val settingsVM: SettingsVM = koinViewModel()
    val searchSheetVM: SearchSheetVM = koinViewModel()

    val settingsState by settingsVM.state.collectAsStateWithLifecycle()
    val searchState by searchSheetVM.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(settingsState.onBoardingDone) {
        if (!settingsState.onBoardingDone) {
            navController.navigate(Route.Onboarding)
        }
    }

    RushTheme(
        state = settingsState.theme
    ) {
        AnimatedContent(
            targetState = settingsState.showPaywall,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!it) {
                NavHost(
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() },
                    popEnterTransition = { fadeIn() },
                    popExitTransition = { fadeOut() },
                    navController = navController,
                    startDestination = Route.SavedPage,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                ) {
                    composable<Route.SavedPage> {
                        val savedVM: SavedVM = koinViewModel()
                        val savedState by savedVM.state.collectAsStateWithLifecycle()

                        SavedPage(
                            state = savedState,
                            notificationAccess = NotificationListener.canAccessNotifications(context),
                            action = savedVM::onAction,
                            onNavigateToLyrics = { navController.navigate(Route.LyricsGraph) },
                            onNavigateToSettings = { navController.navigate(Route.SettingsGraph) },
                            modifier = Modifier.widthIn(max = 700.dp)
                        )
                    }

                    composable<Route.LyricsGraph> {
                        val lyricsVM: LyricsVM = koinViewModel()
                        val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()

                        LyricsGraph(
                            notificationAccess = NotificationListener.canAccessNotifications(context),
                            lyricsState = lyricsState,
                            lyricsAction = lyricsVM::onAction,
                            onDismiss = { navController.navigateUp() },
                            onShare = {
                                navController.navigate(Route.SharePage) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable<Route.SharePage> {
                        val shareVM: ShareVM = koinViewModel()
                        val shareState by shareVM.state.collectAsStateWithLifecycle()

                        SharePage(
                            onDismiss = { navController.navigateUp() },
                            state = shareState,
                            action = shareVM::onAction
                        )
                    }

                    composable<Route.SettingsGraph> {
                        SettingsGraph(
                            notificationAccess = NotificationListener.canAccessNotifications(context),
                            state = settingsState,
                            action = settingsVM::onAction,
                            onNavigateBack = { navController.navigateUp() }
                        )
                    }

                    composable<Route.Onboarding> {
                        Onboarding(
                            onDone = {
                                settingsVM.onAction(SettingsPageAction.OnUpdateOnBoardingDone(true))
                                navController.navigateUp()
                            }
                        )
                    }
                }

                if (searchState.visible) {
                    SearchSheet(
                        state = searchState,
                        action = searchSheetVM::onAction,
                        onClick = {
                            navController.navigate(Route.LyricsGraph) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            } else {
                PaywallPage(
                    isProUser = settingsState.isProUser,
                    onDismissRequest = { settingsVM.onAction(SettingsPageAction.OnDismissPaywall) }
                )
            }
        }
    }
}