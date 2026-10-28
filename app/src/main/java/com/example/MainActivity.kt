package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProgressionScreen
import com.example.ui.screens.RoutinesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TrophyRoomScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PulseBackground
import com.example.ui.theme.PulseBorder
import com.example.ui.theme.PulseSurface
import com.example.ui.theme.PulseSurfaceElevated
import com.example.ui.theme.PulseTextPrimary
import com.example.ui.theme.PulseTextSecondary
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.WorkoutViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: WorkoutViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppHost(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppHost(viewModel: WorkoutViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isWorkoutOpen by viewModel.isWorkoutScreenOpen.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val workoutDuration by viewModel.workoutDurationSeconds.collectAsStateWithLifecycle()

    // Handle back button when not in workout screen: if not on HOME, return to HOME
    BackHandler(enabled = !isWorkoutOpen && selectedTab != MainTab.HOME) {
        viewModel.selectTab(MainTab.HOME)
    }

    if (isWorkoutOpen) {
        ActiveWorkoutScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag("main_app_scaffold"),
            containerColor = PulseBackground,
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    // Floating Mini-Workout Player Bar (if active workout is minimized)
                    AnimatedVisibility(
                        visible = activeSession != null,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.openWorkoutScreen() }
                                .testTag("minimized_workout_player_bar"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = PulseSurfaceElevated),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonLime)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(NeonLime.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            tint = NeonLime,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = activeSession?.title ?: "Workout",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = PulseTextPrimary,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = formatDuration(workoutDuration),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = NeonLime
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonLime,
                                    contentColor = PulseBackground
                                ) {
                                    Text(
                                        text = "Resume",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    // Main 5-Item Navigation Bar
                    NavigationBar(
                        containerColor = PulseSurface,
                        contentColor = PulseTextPrimary,
                        tonalElevation = 8.dp
                    ) {
                        val navItems = listOf(
                            NavigationItemData("Home", Icons.Default.FitnessCenter, MainTab.HOME, "nav_home"),
                            NavigationItemData("Routines", Icons.Default.Assignment, MainTab.ROUTINES, "nav_routines"),
                            NavigationItemData("Progress", Icons.Default.TrendingUp, MainTab.PROGRESSION, "nav_progression"),
                            NavigationItemData("Trophies", Icons.Default.EmojiEvents, MainTab.TROPHIES, "nav_trophies"),
                            NavigationItemData("Settings", Icons.Default.Settings, MainTab.SETTINGS, "nav_settings")
                        )

                        navItems.forEach { item ->
                            val selected = selectedTab == item.tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PulseBackground,
                                    selectedTextColor = ElectricCyan,
                                    indicatorColor = ElectricCyan,
                                    unselectedIconColor = PulseTextSecondary,
                                    unselectedTextColor = PulseTextSecondary
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (selectedTab) {
                    MainTab.HOME -> HomeScreen(viewModel = viewModel)
                    MainTab.ROUTINES -> RoutinesScreen(viewModel = viewModel)
                    MainTab.PROGRESSION -> ProgressionScreen(viewModel = viewModel)
                    MainTab.TROPHIES -> TrophyRoomScreen(viewModel = viewModel)
                    MainTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

data class NavigationItemData(
    val label: String,
    val icon: ImageVector,
    val tab: MainTab,
    val testTag: String
)

private fun formatDuration(seconds: Int): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    else String.format(Locale.US, "%02d:%02d", mins, secs)
}
