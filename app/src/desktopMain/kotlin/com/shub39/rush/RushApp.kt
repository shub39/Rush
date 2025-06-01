package com.shub39.rush

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shub39.rush.core.domain.Route
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.domain.enums.Fonts
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
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Music
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Wrench
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import rush.app.generated.resources.Res
import rush.app.generated.resources.rush_transparent

// Not Completed yet
@Composable
fun RushApp(
    lyricsVM: LyricsVM = koinViewModel(),
    searchSheetVM: SearchSheetVM = koinViewModel(),
    savedVM: SavedVM = koinViewModel()
) {
    val lyricsState by lyricsVM.state.collectAsStateWithLifecycle()
    val searchState by searchSheetVM.state.collectAsStateWithLifecycle()
    val savedState by savedVM.state.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    var currentRoute: Route by remember { mutableStateOf(Route.SavedPage) }

    RushTheme(
        state = Theme(
            appTheme = AppTheme.DARK,
            fonts = Fonts.MANROPE
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
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
                Route.allRoutes.forEach {
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
                                    Route.SavedPage -> FontAwesomeIcons.Solid.Home
                                    Route.SettingsGraph -> FontAwesomeIcons.Solid.Wrench
                                },
                                modifier = Modifier.size(24.dp),
                                contentDescription = "Navigate"
                            )
                        }
                    )
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
                        action = savedVM::onAction,
                        currentSong = lyricsState.song,
                        autoChange = lyricsState.autoChange,
                        showCurrent = false,
                        notificationAccess = true,
                        navigator = {
                            navController.navigate(it) {
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                composable<Route.LyricsGraph> {
                    currentRoute = Route.LyricsGraph

                    LyricsGraph(
                        lyricsState = lyricsState,
                        lyricsAction = lyricsVM::onAction,
                    )
                }

                composable<Route.SettingsGraph> {
                    currentRoute = Route.SettingsGraph

                    SettingsGraph { navController.navigateUp() }
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
    }
}