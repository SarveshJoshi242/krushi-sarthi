package com.krushiadhaar.app.buyer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.krushiadhaar.app.ViewModelFactory
import com.krushiadhaar.app.marketplace.MarketplaceScreen
import com.krushiadhaar.app.profile.ProfileScreen
import com.krushiadhaar.app.ui.theme.GreenPrimary
import com.krushiadhaar.app.ui.theme.GreenSurface
import com.krushiadhaar.app.ui.theme.TextSecondary

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.krushiadhaar.app.buyer.BuyerDashboardScreen
import androidx.compose.material.icons.filled.Home

sealed class BuyerBottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Dashboard    : BuyerBottomNavItem("dashboard_tab", Icons.Default.Home, "Home")
    object History      : BuyerBottomNavItem("history_tab", Icons.Default.History, "History")
    object Profile      : BuyerBottomNavItem("profile_tab", Icons.Default.Person, "Profile")
}

@Composable
fun BuyerMainScreen(
    rootNavController: NavHostController,
    factory: ViewModelFactory? = null,
    onLogout: () -> Unit = {}
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BuyerBottomNavItem.Dashboard,
        BuyerBottomNavItem.History,
        BuyerBottomNavItem.Profile
    )

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
            startDestination = BuyerBottomNavItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BuyerBottomNavItem.Dashboard.route) {
                BuyerDashboardScreen(
                    onNavigateToMarketplace = { bottomNavController.navigate("marketplace_screen") },
                    onNavigateToNotifications = { bottomNavController.navigate("notifications_screen") },
                    onNavigateToChat = { farmer -> bottomNavController.navigate("chat_screen/${android.net.Uri.encode(farmer)}") }
                )
            }
            composable("marketplace_screen") {
                MarketplaceScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    onNavigateToChat = { farmer -> bottomNavController.navigate("chat_screen/${android.net.Uri.encode(farmer)}") }
                )
            }
            composable(
                "chat_screen/{farmerName}",
                arguments = listOf(navArgument("farmerName") { type = NavType.StringType })
            ) { backStackEntry ->
                val farmerName = backStackEntry.arguments?.getString("farmerName") ?: "Farmer"
                ChatScreen(
                    farmerName = farmerName,
                    onNavigateBack = { bottomNavController.popBackStack() }
                )
            }
            composable("notifications_screen") {
                BuyerNotificationsScreen(
                    onNavigateBack = { bottomNavController.popBackStack() }
                )
            }
            composable(BuyerBottomNavItem.History.route) {
                BuyerHistoryScreen()
            }
            composable(BuyerBottomNavItem.Profile.route) {
                ProfileScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    onLogout = onLogout
                )
            }
        }
    }
}
