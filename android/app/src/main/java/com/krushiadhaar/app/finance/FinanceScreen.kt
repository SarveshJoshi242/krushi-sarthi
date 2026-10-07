package com.krushiadhaar.app.finance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.data.remote.api.*
import com.krushiadhaar.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    onNavigateBack: () -> Unit,
    financeApi: FinanceApi? = null // Passed or injected
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Revenue", "Schemes", "Insurance", "Loans")
    
    // In a real app we'd use ViewModel, here we fetch directly for simplicity if no VM
    var schemes by remember { mutableStateOf<List<FinanceScheme>>(emptyList()) }
    var insurance by remember { mutableStateOf<List<FinanceInsurance>>(emptyList()) }
    var loans by remember { mutableStateOf<List<FinanceLoan>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val sRes = financeApi?.getSchemes()
            if (sRes?.isSuccessful == true) sRes.body()?.data?.let { schemes = it }
            
            val iRes = financeApi?.getInsurance()
            if (iRes?.isSuccessful == true) iRes.body()?.data?.let { insurance = it }
            
            val lRes = financeApi?.getLoans()
            if (lRes?.isSuccessful == true) lRes.body()?.data?.let { loans = it }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance & Schemes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = GreenPrimary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        },
                        selectedContentColor = GreenPrimary,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                0 -> {
                    val revenue = com.krushiadhaar.app.GlobalMockData.cropYieldRevenue.value
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Card(colors = CardDefaults.cardColors(containerColor = GreenPrimary), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text("Total Crop Yield Revenue", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("₹$revenue", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 36.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("This reflects total earnings from orders placed by buyers on the Marketplace.", color = TextSecondary, fontSize = 14.sp)
                    }
                }
                1 -> {
                    val displayList = schemes.ifEmpty { listOf(FinanceScheme("1", "PM-Kisan", "Income support for farmers", "All landholding farmers")) }
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(displayList) { scheme ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(scheme.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(scheme.description, fontSize = 14.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Eligibility: ${scheme.eligibility}", fontSize = 12.sp, color = GreenPrimary)
                                }
                            }
                        }
                    }
                }
                2 -> {
                    val displayList = insurance.ifEmpty { listOf(FinanceInsurance("1", "AgriGuard", "Weather & Crop Failure", "2% Premium")) }
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(displayList) { ins ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(ins.provider, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Coverage: ${ins.coverage}", fontSize = 14.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Premium: ${ins.premium}", fontSize = 12.sp, color = OrangePrimary)
                                }
                            }
                        }
                    }
                }
                3 -> {
                    val displayList = loans.ifEmpty { listOf(FinanceLoan("1", "SBI Krushi Loan", "7% p.a.", 500000.0)) }
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(displayList) { loan ->
                            Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(loan.bankName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Interest Rate: ${loan.interestRate}", fontSize = 14.sp, color = TextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Max Amount: ₹${loan.maxAmount}", fontSize = 12.sp, color = GreenPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
