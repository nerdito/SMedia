package com.sebsmedia.smediaviewer

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sebsmedia.smediaviewer.ui.screens.MainScreen
import com.sebsmedia.smediaviewer.ui.screens.PinScreen
import com.sebsmedia.smediaviewer.ui.screens.SettingsScreen
import com.sebsmedia.smediaviewer.ui.screens.VideoPlayerScreen
import com.sebsmedia.smediaviewer.viewmodel.MediaViewModel
import com.sebsmedia.smediaviewer.viewmodel.SettingsViewModel

class App : Application()

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Pin : Screen("pin")
    object Settings : Screen("settings")
    object VideoPlayer : Screen("video_player/{videoUri}") {
        fun createRoute(videoUri: String): String = "video_player/$videoUri"
    }
}

@Composable
fun MediaViewerApp() {
    val navController = rememberNavController()
    val mediaViewModel: MediaViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    val videos by mediaViewModel.videos.collectAsState()
    val isLoading by mediaViewModel.isLoading.collectAsState()
    val selectedVideo by mediaViewModel.selectedVideo.collectAsState()
    val isPinSet by settingsViewModel.isPinSet.collectAsState()
    val selectedFolderUri by settingsViewModel.selectedFolderUri.collectAsState()
    val folderDisplayName by settingsViewModel.folderDisplayName.collectAsState()

    var pendingRoute by remember { mutableStateOf<String?>(null) }
    var showPinForSettings by remember { mutableStateOf(false) }
    var showPinForChangePin by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                videos = videos,
                isLoading = isLoading,
                onSettingsClick = {
                    if (isPinSet) {
                        showPinForSettings = true
                    } else {
                        navController.navigate(Screen.Settings.route)
                    }
                },
                onVideoClick = { video ->
                    mediaViewModel.selectVideo(video)
                    val encodedUri = Uri.encode(video.uri.toString())
                    navController.navigate(Screen.VideoPlayer.createRoute(encodedUri))
                }
            )
        }

        composable(Screen.Pin.route) {
            PinScreen(
                isPinSet = isPinSet,
                onPinVerified = { pin ->
                    settingsViewModel.verifyPin(pin)
                },
                onPinSet = { pin ->
                    settingsViewModel.savePin(pin)
                },
                onSuccess = {
                    when {
                        showPinForSettings -> {
                            showPinForSettings = false
                            navController.navigate(Screen.Settings.route)
                        }
                        showPinForChangePin -> {
                            showPinForChangePin = false
                            // Navigate back to settings after PIN change
                        }
                        else -> {
                            navController.popBackStack()
                        }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                selectedFolderUri = selectedFolderUri?.toString(),
                folderDisplayName = folderDisplayName,
                onBackClick = { navController.popBackStack() },
                onFolderSelected = { uriString, displayName ->
                    val uri = Uri.parse(uriString)
                    settingsViewModel.setSelectedFolder(uri, displayName)
                    mediaViewModel.loadVideos()
                },
                onChangePinClick = {
                    showPinForChangePin = true
                    navController.navigate(Screen.Pin.route)
                }
            )
        }

        composable(Screen.VideoPlayer.route) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("videoUri")
            val videoUri = encodedUri?.let { Uri.parse(Uri.decode(it)) }

            videoUri?.let { uri ->
                VideoPlayerScreen(
                    videoUri = uri,
                    onBackClick = {
                        mediaViewModel.clearSelectedVideo()
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
