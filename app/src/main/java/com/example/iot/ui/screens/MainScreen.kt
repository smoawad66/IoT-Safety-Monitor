package com.example.iot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iot.ui.components.ActuatorCard
import com.example.iot.ui.components.SensorCard
import com.example.iot.viewmodel.SensorViewModel

@Composable
fun MainScreen(viewModel: SensorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Title with gray background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131212)) // Dark gray background
                .padding(top = 16.dp, bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "IoT Safety System",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp, // Increased font size
                color = MaterialTheme.colorScheme.primary
            )
        }
        // Grid of cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SensorCard(
                    title = "Temperature",
                    value = "${viewModel.temperature.floatValue} °C",
                    icon = { Icon(Icons.Default.Thermostat, contentDescription = "Temperature", modifier = Modifier.size(30.dp)) }
                )
            }
            item {
                SensorCard(
                    title = "Humidity",
                    value = "${viewModel.humidity.floatValue} %",
                    icon = { Icon(Icons.Default.WaterDrop, contentDescription = "Humidity", modifier = Modifier.size(30.dp)) }
                )
            }
            item {
                SensorCard(
                    title = "Light",
                    value = viewModel.ldr.intValue.toString(),
                    icon = { Icon(Icons.Default.Lightbulb, contentDescription = "Light", modifier = Modifier.size(30.dp)) }
                )
            }
            item {
                SensorCard(
                    title = "Motion",
                    value = if (viewModel.motion.value) "Detected" else "None",
                    icon = { Icon(Icons.Default.Sensors, contentDescription = "Motion", modifier = Modifier.size(30.dp)) }
                )
            }
            item {
                SensorCard(
                    title = "Gas",
                    value = if (!viewModel.gas.value) "Detected" else "Safe",
                    icon = { Icon(Icons.Default.Warning, contentDescription = "Gas", modifier = Modifier.size(30.dp)) }
                )
            }
            item {
                ActuatorCard(
                    title = "Buzzer",
                    state = viewModel.buzzer.value,
                    overrideState = viewModel.buzzerShutdown.value,
                    onToggleOverride = { viewModel.toggleBuzzerShutdown() },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Buzzer", modifier = Modifier.size(30.dp)) }
                )
            }
            item {
                ActuatorCard(
                    title = "Red LED",
                    state = viewModel.ledRed.value,
                    overrideState = false,
                    onToggleOverride = {},
                    icon = {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Red LED",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
            }
            item {
                ActuatorCard(
                    title = "Green LED",
                    state = viewModel.ledGreen.value,
                    overrideState = false,
                    onToggleOverride = {},
                    icon = {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Green LED",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
            }
            item {
                ActuatorCard(
                    title = "Blue LED",
                    state = viewModel.ledBlue.value,
                    overrideState = !viewModel.ledBlue.value, // Gray out when off
                    onToggleOverride = { viewModel.toggleBlueLed() },
                    icon = {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Blue LED",
                            tint = Color(0xFF2196F3), // Blue tint
                            modifier = Modifier.size(30.dp)
                        )
                    }
                )
            }
            item {
                ActuatorCard(
                    title = "Fan",
                    state = viewModel.fan.value,
                    overrideState = viewModel.fanShutdown.value,
                    onToggleOverride = { viewModel.toggleFanShutdown() },
                    icon = { Icon(Icons.Default.Air, contentDescription = "Fan", modifier = Modifier.size(30.dp)) }
                )
            }
        }
    }
}