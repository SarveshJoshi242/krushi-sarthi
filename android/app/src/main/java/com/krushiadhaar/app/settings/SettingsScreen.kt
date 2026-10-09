package com.krushiadhaar.app.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Preferences", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Language", style = MaterialTheme.typography.labelMedium)
                var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text(com.krushiadhaar.app.GlobalMockData.userLanguage.value)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("English") }, onClick = { com.krushiadhaar.app.GlobalMockData.userLanguage.value = "English"; expanded = false })
                        DropdownMenuItem(text = { Text("Hindi") }, onClick = { com.krushiadhaar.app.GlobalMockData.userLanguage.value = "Hindi"; expanded = false })
                        DropdownMenuItem(text = { Text("Marathi") }, onClick = { com.krushiadhaar.app.GlobalMockData.userLanguage.value = "Marathi"; expanded = false })
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Account Settings", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { /* TODO */ }) {
                    Text("Change Password")
                }
                TextButton(onClick = { /* TODO */ }) {
                    Text("Update Phone Number")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        var showRecoveryKeyDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Security", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { showRecoveryKeyDialog = true }) {
                    Text("Recovery Keys")
                }
            }
        }
        
        if (showRecoveryKeyDialog) {
            AlertDialog(
                onDismissRequest = { showRecoveryKeyDialog = false },
                title = { Text("Your Recovery Key") },
                text = { Text("Your account recovery key is securely stored in your profile. Please check your profile or registration email for the original key. If you lose access, this key is the only way to recover your account.") },
                confirmButton = {
                    TextButton(onClick = { showRecoveryKeyDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}
