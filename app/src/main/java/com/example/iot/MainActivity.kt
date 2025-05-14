package com.example.iot

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iot.aws.AWSIoTManager
import com.example.iot.ui.screens.MainScreen
import com.example.iot.ui.theme.IoTTheme
import com.example.iot.viewmodel.SensorViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var awsIoTManager: AWSIoTManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        awsIoTManager = AWSIoTManager(this)

        setContent {
            IoTTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val viewModel: SensorViewModel = viewModel(
                        factory = SensorViewModelFactory(awsIoTManager)
                    )
                    awsIoTManager.setListener(viewModel)
                    awsIoTManager.connect()
                    MainScreen(viewModel)

                    viewModel.toastMessage.observe(this) { message ->
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        // Start publishing random sensor values
        // CoroutineScope(Dispatchers.IO).launch { publishRandomSensorValues() }
    }

    private suspend fun publishRandomSensorValues() {
        while (true) {
            try {
                // Generate random sensor values
                val temperature = Random.nextFloat() * (35.0f - 15.0f) + 15.0f // 15.0 to 35.0 °C
                val humidity = Random.nextFloat() * (80.0f - 20.0f) + 20.0f // 20.0 to 80.0 %
                val ldr = Random.nextInt(0, 1024) // 0 to 1023
                val motion = Random.nextBoolean() // true or false
                val gas = Random.nextBoolean() // true or false

                // Publish to individual topics
                awsIoTManager.publish(temperature, "sensors/temperature")
                awsIoTManager.publish(humidity, "sensors/humidity")
                awsIoTManager.publish(ldr, "sensors/ldr")
                awsIoTManager.publish(motion, "sensors/motion")
                awsIoTManager.publish(gas, "sensors/gas")

                Log.d("MainActivity", "Published random sensor values: temp=$temperature, hum=$humidity, ldr=$ldr, motion=$motion, gas=$gas")

                // Delay for 5 seconds before next publication
                delay(5000)
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to publish random sensor values", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        awsIoTManager.disconnect()
    }
}

class SensorViewModelFactory(private val awsIoTManager: AWSIoTManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SensorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SensorViewModel(awsIoTManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}