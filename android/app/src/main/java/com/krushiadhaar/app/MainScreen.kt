package com.krushiadhaar.app

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import com.krushiadhaar.app.assistant.VoiceAssistantScreen
import com.krushiadhaar.app.mandi.MandiPricesScreen
import com.krushiadhaar.app.soil.SoilWaterManagementScreen
import com.krushiadhaar.app.soil.SoilHealthScreen
import com.krushiadhaar.app.irrigation.SmartIrrigationScreen
import com.krushiadhaar.app.report.PdfReportScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val labelKey: String) {
    object Home      : BottomNavItem("home_tab", Icons.Default.Home, "home")
    object Pest      : BottomNavItem("pest_tab", Icons.Default.Warning, "pest")
    object PitchCrop : BottomNavItem("pitch_crop_tab", Icons.Default.AddCircle, "pitch_crop")
    object Expenses  : BottomNavItem("expenses_tab", Icons.Default.AccountBalanceWallet, "expenses")
    object Schemes   : BottomNavItem("schemes_tab", Icons.Default.Description, "schemes")
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

    LaunchedEffect(GlobalMockData.pendingNavigationRoute.value) {
        val route = GlobalMockData.pendingNavigationRoute.value
        if (route != null) {
            bottomNavController.navigate(route)
            GlobalMockData.pendingNavigationRoute.value = null
        }
    }

    val currentLang = GlobalMockData.userLanguage.value
    val isMr = currentLang.contains("Marathi", true) || currentLang.contains("मराठी", true)
    val isHi = currentLang.contains("Hindi", true) || currentLang.contains("हिंदी", true)

    fun getNavLabel(item: BottomNavItem): String = when (item) {
        BottomNavItem.Home -> if (isMr) "मुख्य" else if (isHi) "होम" else "Home"
        BottomNavItem.Pest -> if (isMr) "कीड रोग" else if (isHi) "कीट रोग" else "Pest"
        BottomNavItem.PitchCrop -> if (isMr) "पीक विक्री" else if (isHi) "फसल बेचें" else "Pitch Crop"
        BottomNavItem.Expenses -> if (isMr) "खर्च व पाकीट" else if (isHi) "खर्च व बटुआ" else "Expenses"
        BottomNavItem.Schemes -> if (isMr) "योजना" else if (isHi) "योजनाएं" else "Schemes"
    }

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Pest,
        BottomNavItem.PitchCrop,
        BottomNavItem.Expenses,
        BottomNavItem.Schemes
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                items.forEach { item ->
                    val label = getNavLabel(item)
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = label) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
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
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(280)) },
            exitTransition = { fadeOut(animationSpec = tween(240)) },
            popEnterTransition = { fadeIn(animationSpec = tween(280)) },
            popExitTransition = { fadeOut(animationSpec = tween(240)) }
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onNavigateToDiseaseDetection = { bottomNavController.navigate("disease") },
                    onNavigateToManagement      = { bottomNavController.navigate("management") },
                    onNavigateToDocuments       = { bottomNavController.navigate("documents") },
                    onNavigateToFinance         = { bottomNavController.navigate("finance") },
                    onNavigateToExpense         = { bottomNavController.navigate(BottomNavItem.Expenses.route) },
                    onNavigateToProfile         = { bottomNavController.navigate("profile") },
                    onNavigateToMarketplace     = { bottomNavController.navigate("marketplace") },
                    onNavigateToPitchCrop       = { bottomNavController.navigate(BottomNavItem.PitchCrop.route) },
                    onNavigateToCommunity       = { bottomNavController.navigate("community") },
                    onNavigateToVoiceAssistant  = { bottomNavController.navigate("voice_assistant") },
                    onNavigateToMandi           = { bottomNavController.navigate("mandi_prices") },
                    onNavigateToSoilHealth      = { bottomNavController.navigate("soil_water") },
                    onNavigateToIrrigation      = { bottomNavController.navigate("soil_water") },
                    onNavigateToPdfReport       = { bottomNavController.navigate("pdf_report") }
                )
            }
            navigation(startDestination = "pest_home", route = BottomNavItem.Pest.route) {
                composable("pest_home") { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        bottomNavController.getBackStackEntry(BottomNavItem.Pest.route)
                    }
                    val viewModel: com.krushiadhaar.app.disease.DiseaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)
                    PestHomeScreen(
                        onTakePhotoClick = { 
                            viewModel.resetState()
                            bottomNavController.navigate("pest_analysis") 
                        },
                        onUploadClick = { 
                            viewModel.resetState()
                            bottomNavController.navigate("pest_analysis") 
                        },
                        onViewRecentScan = { scan ->
                            viewModel.loadHistoricalReport(scan)
                            bottomNavController.navigate("pest_report")
                        }
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
                    DiagnosisReportScreen(
                        onViewTreatment = { bottomNavController.navigate("pest_treatment") },
                        onDownload = { bottomNavController.navigate("pdf_report") },
                        onBack = { bottomNavController.popBackStack() },
                        viewModel = viewModel
                    )
                }
                composable("pest_treatment") { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        bottomNavController.getBackStackEntry(BottomNavItem.Pest.route)
                    }
                    val viewModel: com.krushiadhaar.app.disease.DiseaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(parentEntry)
                    TreatmentGuideScreen(
                        onBack = { bottomNavController.popBackStack("pest_home", false) },
                        viewModel = viewModel
                    )
                }
            }
            composable(BottomNavItem.PitchCrop.route) {
                SellCropScreen(
                    onNavigateBack = { bottomNavController.navigate(BottomNavItem.Home.route) },
                    onSubmitSuccess = { bottomNavController.navigate(BottomNavItem.Home.route) }
                )
            }
            composable(BottomNavItem.Expenses.route) {
                ExpenseCalculatorScreen(
                    onNavigateBack = { bottomNavController.navigate(BottomNavItem.Home.route) },
                    initialTab = 0
                )
            }
            composable(BottomNavItem.Schemes.route) {
                GovernmentSchemesScreen(onBackClick = { bottomNavController.navigate(BottomNavItem.Home.route) })
            }
            
            composable("profile") {
                ProfileScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    onLogout = onLogout
                )
            }
            
            // Legacy & Direct Routes
            composable("wallet_tab") {
                ExpenseCalculatorScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    initialTab = 0
                )
            }
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
                ExpenseCalculatorScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    initialTab = 1
                )
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
            composable("voice_assistant") {
                VoiceAssistantScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("mandi_prices") {
                MandiPricesScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("soil_water") {
                SoilWaterManagementScreen(onNavigateBack = { bottomNavController.popBackStack() }, initialTab = 0)
            }
            composable("soil_health") {
                SoilWaterManagementScreen(onNavigateBack = { bottomNavController.popBackStack() }, initialTab = 0)
            }
            composable("smart_irrigation") {
                SoilWaterManagementScreen(onNavigateBack = { bottomNavController.popBackStack() }, initialTab = 1)
            }
            composable("pdf_report") {
                PdfReportScreen(onNavigateBack = { bottomNavController.popBackStack() })
            }
            composable("community") {
                com.krushiadhaar.app.community.CommunityScreen(
                    onNavigateBack = { bottomNavController.popBackStack() },
                    onNavigateToChat = { farmerName -> bottomNavController.navigate("chat_screen/${android.net.Uri.encode(farmerName)}") }
                )
            }
        }
    }
}
