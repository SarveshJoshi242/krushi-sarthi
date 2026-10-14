package com.krushiadhaar.app.irrigation

import androidx.compose.runtime.Composable
import com.krushiadhaar.app.soil.SoilWaterManagementScreen

/**
 * Delegator to the unified Soil & Water Management Module (Tab 1: Irrigation)
 */
@Composable
fun SmartIrrigationScreen(
    onNavigateBack: () -> Unit
) {
    SoilWaterManagementScreen(
        onNavigateBack = onNavigateBack,
        initialTab = 1
    )
}
