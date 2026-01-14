package com.shub39.rush.app

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import com.shub39.rush.billing.PaywallPage
import com.shub39.rush.presentation.components.RushTheme
import com.shub39.rush.presentation.lyrics.LyricsGraph
import com.shub39.rush.presentation.onboarding.Onboarding
import com.shub39.rush.presentation.saved.SavedPage
import com.shub39.rush.presentation.searchsheet.SearchSheet
import com.shub39.rush.presentation.setting.SettingsGraph
import com.shub39.rush.presentation.share.SharePage
import com.shub39.rush.viewmodels.GlobalVM
import com.shub39.rush.viewmodels.LyricsVM
import com.shub39.rush.viewmodels.SavedVM
import com.shub39.rush.viewmodels.SearchSheetVM
import com.shub39.rush.viewmodels.SettingsVM
import com.shub39.rush.viewmodels.ShareVM
import com.skydoves.landscapist.coil3.LocalCoilImageLoader
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Serializable
private sealed interface Route {
    @Serializable
    data object SavedPage : Route

    @Serializable
    data object LyricsGraph : Route

    @Serializable
    data object SettingsGraph : Route

    @Serializable
    data object SharePage : Route

    @Serializable
    data object OnboardingPage : Route

    @Serializable
    data object PaywallPage : Route
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val globalVM: GlobalVM = koinViewModel()
    val globalState by globalVM.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        globalVM.onAction(GlobalAction.OnCheckNotificationAccess(context))
    }

    LaunchedEffect(globalState.onBoardingDone, globalState.showPaywall) {
        if (!globalState.onBoardingDone) {
            navController.navigate(Route.OnboardingPage)
        }
        if (globalState.showPaywall) {
            navController.navigate(Route.PaywallPage)
        }
    }

    CompositionLocalProvider(
        LocalCoilImageLoader provides koinInject<ImageLoader>()
    ) {
        RushTheme(theme = globalState.theme) {
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
                        notificationAccess = globalState.notificationAccess,
                        onAction = savedVM::onAction,
                        onNavigateToLyrics = { navController.navigate(Route.LyricsGraph) },
                        onNavigateToSettings = { navController.navigate(Route.SettingsGraph) },
                        modifier = Modifier.widthIn(max = 700.dp)
                    )
                }

                composable<Route.LyricsGraph> {
                    val lyricsVM: LyricsVM = koinViewModel()
                    val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()

                    LyricsGraph(
                        notificationAccess = globalState.notificationAccess,
                        lyricsState = lyricsState,
                        lyricsAction = lyricsVM::onAction,
                        onDismiss = {
                            navController.navigateUp()
                        },
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
                        onAction = shareVM::onAction,
                        isProUser = globalState.isProUser,
                        onShowPaywall = { globalVM.onAction(GlobalAction.OnTogglePaywall) },
                    )
                }

                composable<Route.SettingsGraph> {
                    val settingsVM: SettingsVM = koinViewModel()
                    val settingsState by settingsVM.state.collectAsStateWithLifecycle()

                    SettingsGraph(
                        notificationAccess = globalState.notificationAccess,
                        state = settingsState,
                        action = settingsVM::onAction,
                        onNavigateBack = { navController.navigateUp() },
                        isProUser = globalState.isProUser,
                        onShowPaywall = { globalVM.onAction(GlobalAction.OnTogglePaywall) },
                    )
                }

                composable<Route.OnboardingPage> {
                    Onboarding(
                        onDone = {
                            globalVM.onAction(GlobalAction.OnUpdateOnboardingDone(true))
                            navController.navigateUp()
                        },
                        notificationAccess = globalState.notificationAccess,
                        onUpdateNotificationAccess = {
                            globalVM.onAction(GlobalAction.OnCheckNotificationAccess(context))
                        }
                    )
                }

                composable<Route.PaywallPage> {
                    PaywallPage(
                        isProUser = globalState.isProUser,
                        onDismissRequest = {
                            navController.navigateUp()
                            globalVM.onAction(GlobalAction.OnTogglePaywall)
                        }
                    )
                }
            }

            val searchSheetVM: SearchSheetVM = koinViewModel()
            val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
            SearchSheet(
                state = searchState,
                onAction = searchSheetVM::onAction,
                onNavigateToLyrics = {
                    navController.navigate(Route.LyricsGraph) {
                        launchSingleTop = true
                    }
                },
                sheetState = rememberModalBottomSheetState()
            )
        }
    }
}