package com.shub39.rush

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.core.presentation.RushTheme
import com.shub39.rush.lyrics.presentation.LyricsGraph
import com.shub39.rush.lyrics.presentation.SettingsGraph
import com.shub39.rush.lyrics.presentation.saved.SavedPage
import com.shub39.rush.lyrics.presentation.saved.SavedPageAction
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheet
import com.shub39.rush.lyrics.presentation.search_sheet.SearchSheetAction
import com.shub39.rush.lyrics.presentation.viewmodels.LyricsVM
import com.shub39.rush.lyrics.presentation.viewmodels.SavedVM
import com.shub39.rush.lyrics.presentation.viewmodels.SearchSheetVM
import com.shub39.rush.lyrics.presentation.viewmodels.SettingsVM
import com.shub39.rush.lyrics.presentation.viewmodels.ShareVM
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Download
import compose.icons.fontawesomeicons.solid.Music
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.WindowClose
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rush.app.generated.resources.Res
import rush.app.generated.resources.rush_transparent

// TODO: Not Completed yet
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun RushApp() {
    val lyricsVM: LyricsVM = koinViewModel()
    val searchSheetVM: SearchSheetVM = koinViewModel()
    val savedVM: SavedVM = koinViewModel()
    val settingsVM: SettingsVM = koinViewModel()
    val shareVM: ShareVM = koinViewModel()

    val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()
    val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
    val settingsState by settingsVM.state.collectAsStateWithLifecycle()
    val savedState by savedVM.state.collectAsStateWithLifecycle()
    val shareState by shareVM.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    var currentRoute: Route by remember { mutableStateOf(Route.SavedPage) }

    var fullscreen by remember { mutableStateOf(true) }
    var isHovered by remember { mutableStateOf(false) }

    RushTheme(
        state = settingsState.theme
    ) {
        Box {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedVisibility(
                    visible = fullscreen
                ) {
                    NavigationRail(
                        windowInsets = WindowInsets(top = 16.dp),
                        header = {
                            FloatingActionButton(
                                onClick = { searchSheetVM.onAction(SearchSheetAction.OnToggleSearchSheet) }
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            FloatingActionButton(
                                onClick = { savedVM.onAction(SavedPageAction.OnToggleAutoChange) },
                                containerColor = if (!lyricsState.autoChange) {
                                    FloatingActionButtonDefaults.containerColor
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.rush_transparent),
                                    contentDescription = "App Icon",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    ) {
                        listOf(
                            Route.LyricsGraph,
                            Route.SavedPage
                        ).forEach {
                            NavigationRailItem(
                                selected = currentRoute == it,
                                onClick = {
                                    if (currentRoute != it) {
                                        navController.navigate(it) { launchSingleTop = true }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = when (it) {
                                            Route.LyricsGraph -> FontAwesomeIcons.Solid.Music
                                            else -> FontAwesomeIcons.Solid.Download
                                        },
                                        modifier = Modifier.size(24.dp),
                                        contentDescription = "Navigate"
                                    )
                                }
                            )
                        }
                    }
                }

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
                        currentRoute = Route.SavedPage

                        SavedPage(
                            state = savedState,
                            extractedColors = lyricsState.extractedColors,
                            action = savedVM::onAction,
                            currentSong = lyricsState.song,
                            autoChange = lyricsState.autoChange,
                            showCurrent = false,
                            notificationAccess = true,
                            onNavigateToLyrics = { navController.navigate(Route.LyricsGraph) { launchSingleTop = true } },
                            onNavigateToSettings = { navController.navigate(Route.SettingsGraph) { launchSingleTop = true } },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable<Route.LyricsGraph> {
                        currentRoute = Route.LyricsGraph

                        LyricsGraph(
                            lyricsState = lyricsState,
                            lyricsAction = lyricsVM::onAction,
                            notificationAccess = true,
                            shareState = shareState,
                            shareAction = shareVM::onAction,
                        )
                    }

                    composable<Route.SettingsGraph> {
                        currentRoute = Route.SettingsGraph

                        SettingsGraph(
                            notificationAccess = true,
                            state = settingsState,
                            action = settingsVM::onAction,
                            onNavigateBack = { navController.navigateUp() }
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
            }

            FloatingActionButton(
                onClick = { fullscreen = !fullscreen },
                modifier = Modifier
                    .onPointerEvent(eventType = PointerEventType.Exit) { isHovered = false }
                    .onPointerEvent(eventType = PointerEventType.Enter) { isHovered = true }
                    .padding(12.dp)
                    .align(Alignment.BottomStart),
                containerColor = if (fullscreen) {
                    MaterialTheme.colorScheme.background
                } else Color.Transparent,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
            ) {
                if (isHovered) {
                    Icon(
                        imageVector = if (fullscreen) {
                            FontAwesomeIcons.Solid.WindowClose
                        } else {
                            Icons.Default.Menu
                        },
                        contentDescription = "Open Sidebar",
                        modifier = Modifier.size(24.dp)
                    )
                } else if (fullscreen) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.WindowClose,
                        contentDescription = "Close Sidebar",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}