package com.krushiadhaar.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krushiadhaar.app.disease.DiseaseDetectionScreen
import com.krushiadhaar.app.management.CropManagementScreen
import com.krushiadhaar.app.management.ExpenseCalculatorScreen
import com.krushiadhaar.app.marketplace.MarketplaceScreen
import com.krushiadhaar.app.marketplace.SellCropScreen
import com.krushiadhaar.app.profile.ProfileScreen
import com.krushiadhaar.app.documents.DocumentsScreen
import com.krushiadhaar.app.settings.SettingsScreen
import com.krushiadhaar.app.ui.theme.*

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home   : BottomNavItem("home_tab", Icons.Default.Home, "Home")
    object Market : BottomNavItem("market_tab", Icons.Default.ShoppingCart, "Market")
    object AITool : BottomNavItem("aitool_tab", Icons.Default.Psychology, "AI tool")
    object Profile: BottomNavItem("profile_tab", Icons.Default.Person, "Profile")
}

@Composable
fun MainScreen(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(BottomNavItem.Home, BottomNavItem.Market, BottomNavItem.AITool, BottomNavItem.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (currentRoute == item.route) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            selectedTextColor = GreenPrimary,
                            indicatorColor = GreenSurface,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
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
                    onNavigateToManagement      = { bottomNavController.navigate("management") },
                    onNavigateToDocuments       = { bottomNavController.navigate("documents") },
                    onNavigateToExpense         = { bottomNavController.navigate("expense") }
                )
            }
            composable(BottomNavItem.Market.route) {
                MarketplaceScreen(
                    onNavigateBack  = { bottomNavController.popBackStack() },
                    onNavigateToSell = { bottomNavController.navigate("sell_crop") }
                )
            }
            composable(BottomNavItem.AITool.route) {
                DiseaseDetectionScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("disease") {
                DiseaseDetectionScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("management") {
                CropManagementScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("expense") {
                ExpenseCalculatorScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("documents") {
                DocumentsScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("sell_crop") {
                SellCropScreen(
                    onNavigateBack  = { bottomNavController.popBackStack() },
                    onSubmitSuccess = { bottomNavController.popBackStack() }
                )
            }
        }
    }
}
