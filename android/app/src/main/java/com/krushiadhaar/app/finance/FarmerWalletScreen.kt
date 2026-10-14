package com.krushiadhaar.app.finance

import androidx.compose.runtime.Composable
import com.krushiadhaar.app.management.ExpenseCalculatorScreen

/**
 * Delegator to the unified Wallet & Expenses Hub (Tab 0: Wallet & Passbook)
 */
@Composable
fun FarmerWalletScreen(
    onBackClick: () -> Unit = {}
) {
    ExpenseCalculatorScreen(
        onNavigateBack = onBackClick,
        initialTab = 0
    )
}
