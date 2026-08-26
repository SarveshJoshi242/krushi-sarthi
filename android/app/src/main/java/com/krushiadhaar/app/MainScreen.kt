package com.krushiadhaar.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krushiadhaar.app.profile.ProfileScreen
import com.krushiadhaar.app.settings.SettingsScreen
import com.krushiadhaar.app.marketplace.MarketplaceScreen
import com.krushiadhaar.app.marketplace.SellCropScreen
import com.krushiadhaar.app.documents.DocumentsScreen
import com.krushiadhaar.app.management.CropManagementScreen
import com.krushiadhaar.app.disease.DiseaseDetectionScreen

sealed class BottomNavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    object Home : BottomNavItem("home_tab", Icons.Default.Home, "Home")
    object Market : BottomNavItem("market_tab", Icons.Default.ShoppingCart, "Market")
    object Profile : BottomNavItem("profile_tab", Icons.Default.Person, "Profile")
    object Settings : BottomNavItem("settings_tab", Icons.Default.Settings, "Settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Market,
        BottomNavItem.Profile,
        BottomNavItem.Settings
    )

    val isTopLevelDestination = items.any { it.route == currentRoute }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Krushi-Adhaar") },
                navigationIcon = {
                    if (!isTopLevelDestination && currentRoute != null) {
                        IconButton(onClick = { bottomNavController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToDiseaseDetection = { bottomNavController.navigate("disease") },
                    onNavigateToManagement = { bottomNavController.navigate("management") },
                    onNavigateToDocuments = { bottomNavController.navigate("documents") }
                )
            }
            composable(BottomNavItem.Market.route) {
                MarketplaceScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    onNavigateToSell = { bottomNavController.navigate("sell_crop") },
                    viewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            
            // Sub-routes within the main flow
            composable("disease") {
                val vm: com.krushiadhaar.app.presentation.disease.DiseaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                DiseaseDetectionScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("management") {
                CropManagementScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("documents") {
                DocumentsScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("sell_crop") {
                SellCropScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    onSubmitSuccess = { bottomNavController.popBackStack() }
                )
            }
        }
    }
}
