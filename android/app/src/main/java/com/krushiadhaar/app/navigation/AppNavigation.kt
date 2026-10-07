package com.krushiadhaar.app.navigation
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krushiadhaar.app.auth.*
import com.krushiadhaar.app.MainScreen
import com.krushiadhaar.app.ViewModelFactory
import com.krushiadhaar.app.presentation.MainViewModel
import com.krushiadhaar.app.domain.model.AuthenticationState

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Language : Screen("language")
    object Step1 : Screen("step1")
    object Step2 : Screen("step2")
    object Analysis : Screen("analysis")
    object Main : Screen("main")
    object BuyerMain : Screen("buyer_main")
}

@Composable
fun AppNavigation(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val authState by mainViewModel.authState.collectAsState()

    if (authState == AuthenticationState.Loading) {
        return
    }

    val startRoute = if (authState == AuthenticationState.Authenticated) Screen.Main.route else Screen.Splash.route

    LaunchedEffect(authState) {
        if (authState == AuthenticationState.Unauthenticated && navController.currentDestination?.route != Screen.Splash.route) {
            navController.navigate(Screen.Splash.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = startRoute) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onGetStarted = { navController.navigate(Screen.Step1.route) }
            )
        }

        composable(Screen.Step1.route) {
            Step1Screen(
                onNext = { navController.navigate(Screen.Step2.route) },
                onLoginAsFarmer = {
                    navController.navigate(Screen.Main.route) { popUpTo(0) }
                },
                onLoginAsBuyer = {
                    navController.navigate(Screen.BuyerMain.route) { popUpTo(0) }
                }
            )
        }
        composable(Screen.Step2.route) {
            Step2Screen(
                onAnalyze = { navController.navigate(Screen.Analysis.route) }
            )
        }
        composable(Screen.Analysis.route) {
            AnalysisLoaderScreen(
                onAnalysisComplete = {
                    navController.navigate(Screen.Main.route) { popUpTo(0) }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                rootNavController = navController, 
                factory = factory,
                onLogout = { 
                    mainViewModel.logout()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.BuyerMain.route) {
            com.krushiadhaar.app.buyer.BuyerMainScreen(
                rootNavController = navController,
                factory = factory,
                onLogout = { 
                    mainViewModel.logout()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
