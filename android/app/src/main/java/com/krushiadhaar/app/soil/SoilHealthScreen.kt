package com.krushiadhaar.app.soil

import androidx.compose.runtime.Composable

/**
 * Delegator to the unified Soil & Water Management Module
 */
@Composable
fun SoilHealthScreen(
    onNavigateBack: () -> Unit
) {
    SoilWaterManagementScreen(
        onNavigateBack = onNavigateBack,
        initialTab = 0
    )
}
