package ir.amirroid.filemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.amirroid.filemanager.ui.features.folder.FolderScreen
import ir.amirroid.filemanager.ui.features.home.HomeScreen
import ir.amirroid.filemanager.ui.features.media.MediaScreen
import ir.amirroid.filemanager.ui.features.search.SearchScreen
import ir.amirroid.filemanager.ui.theme.FileManagerTheme
import ir.amirroid.filemanager.utils.AppPages
import ir.amirroid.filemanager.utils.MediaTypes


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(navController = navController,
        startDestination = AppPages.HomeScreen.route,
        enterTransition = { slideInHorizontally(tween(200)) { 200 } + fadeIn(tween(200)) },
        exitTransition = { slideOutHorizontally(tween(200)) { -200 } + fadeOut(tween(200)) },
        popExitTransition = { slideOutHorizontally(tween(200)) { 200 } + fadeOut(tween(200)) },
        popEnterTransition = { slideInHorizontally(tween(200)) { -200 } + fadeIn(tween(200)) }
    ) {
        composable(AppPages.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(
            AppPages.FolderScreen.route + "?path={path}",
            arguments = listOf(navArgument("path") {
                type = NavType.StringType
            })
        ) {
            val path = it.arguments?.getString("path")!!
            FolderScreen(path, navController)
        }
        composable(
            AppPages.SearchScreen.route
        ) {
            SearchScreen(navController)
        }
        composable(
            AppPages.SearchScreen.route + "?type={type}",
            arguments = listOf(
                navArgument("type") {
                    type = NavType.IntType
                }
            )
        ) {
            val type = MediaTypes.values()[it.arguments?.getInt("type") ?: 0]
            MediaScreen(
                type,
                navController
            )
        }
    }
}