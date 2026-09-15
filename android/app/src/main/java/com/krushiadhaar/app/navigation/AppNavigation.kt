package com.krushiadhaar.app.navigation
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.krushiadhaar.app.auth.LoginScreen
import com.krushiadhaar.app.auth.RegisterScreen
import com.krushiadhaar.app.MainScreen
import com.krushiadhaar.app.onboarding.OnboardingScreen
import com.krushiadhaar.app.ViewModelFactory
import com.krushiadhaar.app.presentation.MainViewModel
import com.krushiadhaar.app.domain.model.AuthenticationState

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
}

@Composable
fun AppNavigation(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel(factory = factory)
    val authState by mainViewModel.authState.collectAsState()

    if (authState == AuthenticationState.Loading) {
        // Show Splash/Loading
        return
    }

    val startRoute = if (authState == AuthenticationState.Authenticated) Screen.Main.route else Screen.Onboarding.route

    NavHost(navController = navController, startDestination = startRoute) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel(factory = factory),
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { 
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel(factory = factory),
                onRegisterSuccess = { 
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(rootNavController = navController)
        }
    }
}
