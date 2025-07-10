package com.shub39.rush.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shub39.rush.core.domain.data_classes.Theme
import com.shub39.rush.core.domain.enums.AppTheme
import com.shub39.rush.core.presentation.RushTheme
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import rush.app.generated.resources.Res
import rush.app.generated.resources.app_icon

@Serializable
private sealed interface OnboardingRoutes {
    @Serializable
    data object Welcome : OnboardingRoutes

    @Serializable
    data object ShareDemo : OnboardingRoutes

    @Serializable
    data object Permissions : OnboardingRoutes

    companion object {
        val routes = listOf(
            Welcome,
            ShareDemo,
            Permissions
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnboardingGraph(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit
) {
    val pagerState = rememberPagerState { OnboardingRoutes.routes.size }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (it) {
                0 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.app_icon),
                            contentDescription = "App Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(300.dp)
                        )

                        Text(
                            text = "Welcome to Rush!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Search, Save and Share your favorite lyrics",
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = {},
                            shapes = ButtonShapes(
                                shape = CircleShape,
                                pressedShape = RoundedCornerShape(10.dp)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next"
                            )
                        }

                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RushTheme(
        state = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        Surface {
            OnboardingGraph(
                state = OnboardingState(),
                onAction = {}
            )
        }
    }
}