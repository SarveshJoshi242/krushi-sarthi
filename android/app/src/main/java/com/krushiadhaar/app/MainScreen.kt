package com.krushiadhaar.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
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
import androidx.navigation.compose.navigation
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.krushiadhaar.app.disease.DiseaseDetectionScreen
import com.krushiadhaar.app.management.CropManagementScreen
import com.krushiadhaar.app.management.ExpenseCalculatorScreen
import com.krushiadhaar.app.marketplace.MarketplaceScreen
import com.krushiadhaar.app.marketplace.SellCropScreen
import com.krushiadhaar.app.profile.ProfileScreen
import com.krushiadhaar.app.documents.DocumentsScreen
import com.krushiadhaar.app.finance.FinanceScreen
import com.krushiadhaar.app.settings.SettingsScreen
import com.krushiadhaar.app.ui.theme.*
import androidx.compose.ui.platform.LocalContext

import com.krushiadhaar.app.ViewModelFactory

import com.krushiadhaar.app.disease.PestHomeScreen
import com.krushiadhaar.app.disease.PestAnalysisScreen
import com.krushiadhaar.app.disease.DiagnosisReportScreen
import com.krushiadhaar.app.disease.TreatmentGuideScreen
import com.krushiadhaar.app.finance.FarmerWalletScreen
import com.krushiadhaar.app.schemes.GovernmentSchemesScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home     : BottomNavItem("home_tab", Icons.Default.Home, "Home")
    object Pest     : BottomNavItem("pest_tab", Icons.Default.Warning, "Pest")
    object Wallet   : BottomNavItem("wallet_tab", Icons.Default.AccountBalanceWallet, "Wallet")
    object Schemes  : BottomNavItem("schemes_tab", Icons.Default.Description, "Schemes")
}

@Composable
fun MainScreen(
    rootNavController: NavHostController,
    factory: ViewModelFactory? = null,
    onLogout: () -> Unit = {}
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(BottomNavItem.Home, BottomNavItem.Pest, BottomNavItem.Wallet, BottomNavItem.Schemes)

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
                    onNavigateToFinance         = { bottomNavController.navigate("finance") },
                    onNavigateToExpense         = { bottomNavController.navigate("expense") },
                    onNavigateToProfile         = { bottomNavController.navigate("profile") },
                    onNavigateToMarketplace     = { bottomNavController.navigate("marketplace") },
                    onNavigateToPitchCrop       = { bottomNavController.navigate("sell_crop") }
                )
            }
            navigation(startDestination = "pest_home", route = BottomNavItem.Pest.route) {
                composable("pest_home") {
                    PestHomeScreen(
                        onTakePhotoClick = { bottomNavController.navigate("pest_analysis") },
                        onUploadClick = { bottomNavController.navigate("pest_analysis") }
                    )
                }
                composable("pest_analysis") { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        bottomNavController.getBackStackEntry(BottomNavItem.Pest.route)
                    }
                    val viewModel: com.krushiadhaar.app.disease.DiseaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)
                    PestAnalysisScreen(
                        onAnalysisComplete = { bottomNavController.navigate("pest_report") },
                        viewModel = viewModel
                    )
                }
                composable("pest_report") { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        bottomNavController.getBackStackEntry(BottomNavItem.Pest.route)
                    }
                    val viewModel: com.krushiadhaar.app.disease.DiseaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)
                    // We need to pass the result to DiagnosisReportScreen or let it read from ViewModel
                    DiagnosisReportScreen(
                        onViewTreatment = { bottomNavController.navigate("pest_treatment") }
                    )
                }
                composable("pest_treatment") { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        bottomNavController.getBackStackEntry(BottomNavItem.Pest.route)
                    }
                    val viewModel: com.krushiadhaar.app.disease.DiseaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)
                    TreatmentGuideScreen(
                        onBack = { bottomNavController.popBackStack("pest_home", false) }
                    )
                }
            }
            composable(BottomNavItem.Wallet.route) {
                FarmerWalletScreen(onBackClick = { bottomNavController.popBackStack() })
            }
            composable(BottomNavItem.Schemes.route) {
                GovernmentSchemesScreen(onBackClick = { bottomNavController.popBackStack() })
            }
            
            composable("profile") {
                ProfileScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    onLogout = onLogout
                )
            }
            
            // Legacy/Extra Modules
            composable("marketplace") {
                MarketplaceScreen(
                    onNavigateToChat = { farmerName -> bottomNavController.navigate("chat_screen/${android.net.Uri.encode(farmerName)}") },
                    onNavigateBack = { bottomNavController.popBackStack() },
                    isFarmer = true
                )
            }
            composable(
                "chat_screen/{farmerName}",
                arguments = listOf(androidx.navigation.navArgument("farmerName") { type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val farmerName = backStackEntry.arguments?.getString("farmerName") ?: "Farmer"
                com.krushiadhaar.app.buyer.ChatScreen(
                    farmerName = farmerName,
                    onNavigateBack = { bottomNavController.popBackStack() }
                )
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
            composable("finance") {
                val context = LocalContext.current
                val financeApi = (context.applicationContext as KrushiAdhaarApplication).financeApi
                FinanceScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    financeApi = financeApi
                )
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
