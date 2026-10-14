package com.krushiadhaar.app.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.krushiadhaar.app.ui.theme.GreenDark
import com.krushiadhaar.app.ui.theme.GreenPrimary
import com.krushiadhaar.app.ui.theme.GreenSurface
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnalysisLoaderScreen(
    onAnalysisComplete: () -> Unit = {}
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var currentStepIndex by remember { mutableIntStateOf(0) }

    val steps = listOf(
        "🛰️ Fetching Live Weather & Radar...",
        "🌱 Analyzing Soil NPK & pH Indices...",
        "🛰️ Processing Satellite Crop Vigor (NDVI)...",
        "💧 Computing Soil Moisture & Irrigation Schedule...",
        "🌾 Finalizing Krishi-Aadhar Farm Diagnostics...",
        "✓ Farm Analysis Complete!"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 150, easing = LinearEasing),
        label = "animatedProgress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        for (i in 1..100) {
            delay(35)
            progress = i / 100f
            currentStepIndex = minOf(steps.size - 1, (progress * steps.size).toInt())
        }
        delay(400)
        onAnalysisComplete()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.krushiadhaar.app.R.drawable.ai_analysing_after_login),
            contentDescription = "AI Analyzing Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        // Dark gradient overlay for text clarity
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.58f))
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AI Farm Diagnostics",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Krishi-Aadhar Neural Engine is configuring your field",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 36.dp)
            )

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    color = GreenPrimary,
                    trackColor = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(110.dp),
                    strokeWidth = 7.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Smooth animated step transitions
            AnimatedContent(
                targetState = steps[currentStepIndex],
                transitionSpec = {
                    (fadeIn(animationSpec = tween(250)) + slideInVertically { height -> height / 2 })
                        .togetherWith(fadeOut(animationSpec = tween(200)) + slideOutVertically { height -> -height / 2 })
                },
                label = "step_text"
            ) { stepText ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = stepText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GreenPrimary,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}
