package com.example.iot.ui.components

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun ActuatorCard(
    title: String,
    state: Boolean,
    overrideState: Boolean,
    onToggleOverride: () -> Unit,
    icon: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .alpha(if (title in arrayOf("Buzzer", "Fan") && overrideState || title == "Blue LED" && overrideState) 0.5f else 1.0f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Main content: icon, title, state
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Icon at the top
                icon()

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Control element based on actuator type
                if (title == "Blue LED") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (state) "On" else "Off",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (state) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Switch(
                            checked = state,
                            onCheckedChange = {
                                Log.d("ActuatorCard", "Toggling Blue LED to ${!state}")
                                onToggleOverride()
                            },
//                            colors = switchColors,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                } else {
                    // Status display for other actuators
                    Text(
                        text = if(overrideState) "Force Stopped" else if (state) "On" else "Off",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Medium,
                        color = if (state) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Shutdown icon for Buzzer and Fan
            if (title in arrayOf("Buzzer", "Fan")) {
                IconButton(
                    onClick = {
                        Log.d("ActuatorCard", "Toggling $title override to ${!overrideState}")
                        onToggleOverride()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Toggle Force Stop",
                        tint = if (overrideState) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (overrideState) {
                    Text(
                        text = "Force Stopped",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(top = 6.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}