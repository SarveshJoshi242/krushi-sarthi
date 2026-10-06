package com.krushiadhaar.app.disease

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.krushiadhaar.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PestQuestionnaireScreen(
    onNext: () -> Unit = {},
    onBack: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Question 1 of 5", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        containerColor = AppBackground,
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                    Text("Not sure? Skip this question", color = GreenPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = onBack) {
                        Text("<- Back", color = TextPrimary)
                    }
                    Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)) {
                        Text("Next ->", color = AppSurface)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            LinearProgressIndicator(
                progress = 0.2f,
                modifier = Modifier.fillMaxWidth(),
                color = GreenPrimary,
                trackColor = AppDivider
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Are there tiny holes or chewed margins visible on these leaves?",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenSurface)
            ) {
                Text("Yes", color = GreenText, style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedLight)
            ) {
                Text("No", color = RedError, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
