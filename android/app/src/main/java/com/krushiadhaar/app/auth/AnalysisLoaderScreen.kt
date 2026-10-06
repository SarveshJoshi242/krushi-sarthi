package com.krushiadhaar.app.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.GreenDark
import com.krushiadhaar.app.ui.theme.GreenPrimary
import kotlinx.coroutines.delay

@Composable
fun AnalysisLoaderScreen(
    onAnalysisComplete: () -> Unit = {}
) {
    var progress by remember { mutableStateOf(0f) }
    var currentStepIndex by remember { mutableStateOf(0) }

    val steps = listOf(
        "Fetching Weather Data...",
        "Analyzing Satellite Imagery...",
        "Reading Soil Information...",
        "Predicting Expected Yield...",
        "Done"
    )

    LaunchedEffect(Unit) {
        for (i in 1..100) {
            delay(40)
            progress = i / 100f
            currentStepIndex = (progress * (steps.size - 1)).toInt()
        }
        delay(500)
        onAnalysisComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenDark)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AI is Analyzing Your Farm",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        CircularProgressIndicator(
            color = GreenPrimary,
            modifier = Modifier.size(80.dp),
            strokeWidth = 6.dp
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = steps[currentStepIndex],
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = GreenPrimary,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}
