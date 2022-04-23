package com.compilinghappen.portfolio

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.compilinghappen.portfolio.ui.theme.PortfolioTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.compilinghappen.portfolio.auth.SignInScreen
import com.compilinghappen.portfolio.auth.SignUpScreen
import com.compilinghappen.portfolio.profile.ProfileScreen


sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen(route = "home", title = "Главная", icon = Icons.Filled.Home)
    object Explore : Screen(route = "explore", title = "Исследование", icon = Icons.Filled.Search)
    object Profile : Screen(route = "profile", title = "Профиль", icon = Icons.Filled.Person)

    object AlbumDetails :
        Screen(route = "album_details", title = "Детали", icon = Icons.Filled.Close)
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PortfolioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PortfolioApp()
                }
            }
        }
    }
}


@Composable
fun PortfolioApp() {
    val context = LocalContext.current
    val navController = rememberNavController()

    var loginSucceeded by rememberSaveable { mutableStateOf(false) }
    var isRegister by rememberSaveable { mutableStateOf(false) }

    if (loginSucceeded || UserToken != null) {
        Scaffold(
            bottomBar = { BottomBar(navController) },
        ) { innerPadding ->
            BottomBarMain(navController, modifier = Modifier.padding(innerPadding))
        }
    }
    else {
        if (!isRegister) {
            SignInScreen(
                signUpSucceeded = {
                    ApiUtils.saveUserToken(context, UserToken!!)
                    loginSucceeded = true
                },
                switchedToRegister = {isRegister = true}
            )
        }
        else {
            SignUpScreen(onRegistered = {
                ApiUtils.saveUserToken(context, UserToken!!)
                loginSucceeded = true
            })
        }

    }
}

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Explore,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        elevation = 5.dp
    ) {
        items.map {
            BottomNavigationItem(
                selected = it.route == currentRoute,
                onClick = { navController.navigate(it.route) },
                icon = { Icon(it.icon, contentDescription = null) },
                label = { Text(it.title) }
            )
        }
    }
}

@Composable
fun BottomBarMain(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Explore.route) {
            ExploreScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen(onAlbumClicked = {
                navController.navigate("${Screen.AlbumDetails.route}/${it.id}")
                Log.d("MainActivity", "Navigating")
            })
        }
        composable(
            route = "${Screen.AlbumDetails.route}/{albumId}",
            arguments = listOf(
                navArgument("albumId") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val albumId = entry.arguments?.getInt("albumId")!!
            AlbumDetailsScreen(albumId)
        }
    }
}
