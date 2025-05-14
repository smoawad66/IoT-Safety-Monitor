package com.example.iot.aws

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.amazonaws.AmazonClientException
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import com.amazonaws.regions.Regions
import com.example.iot.MainActivity
import com.example.iot.SensorDataListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class AWSIoTManager(private val context: Context) {
    private val mqttManager = AWSIotMqttManager("android-client", AWSConfig.IOT_ENDPOINT)
    private var listener: SensorDataListener? = null
    var isConnected = false

    fun setListener(listener: SensorDataListener) { this.listener = listener }

    fun connect() {
        val credentialsProvider = CognitoCachingCredentialsProvider(
            context,
            AWSConfig.COGNITO_POOL_ID,
            Regions.fromName(AWSConfig.AWS_REGION)
        )
        mqttManager.connect(credentialsProvider) { status, throwable ->
            when (status) {
                AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected -> {
                    MainScope().launch { Toast.makeText(context, "Connected to server!", Toast.LENGTH_SHORT).show() }
                    isConnected = true
                    Log.i("iot", "Connected to AWS via MQTT.")
                    subscribeToTopics()
                }
                AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost-> {
                    if (!isConnected) {
                        MainScope().launch { Toast.makeText(context, "Failed to connect to server!", Toast.LENGTH_SHORT).show() }
                    }
                    Log.e("iot", "Connection failed or lost.", throwable)
                    isConnected = false
                }
                else -> {
                    Log.d("iot", "MQTT status: $status")
                }
            }
        }
    }

    private fun subscribeToTopics() {
        val topics = listOf(
            "sensors/temperature",
            "sensors/humidity",
            "sensors/light",
            "sensors/motion",
            "sensors/gas",

            "actuators/buzzer",
            "actuators/led_red",
            "actuators/led_green"
        )
        for (topic in topics) {
            mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0) { topic, data ->
                val message = String(data)
                handleMessage(topic, message)
            }
        }
    }

    private fun handleMessage(topic: String, message: String) {
        try {
            val json = JSONObject(message)
            when (topic) {
                "sensors/temperature" -> listener?.onTemperatureReceived(json.getDouble("value").toFloat())
                "sensors/humidity" -> listener?.onHumidityReceived(json.getDouble("value").toFloat())
                "sensors/light" -> listener?.onLdrReceived(json.getInt("value"))
                "sensors/motion" -> listener?.onMotionReceived(json.getBoolean("value"))
                "sensors/gas" -> listener?.onGasReceived(json.getBoolean("value"))

                "actuators/buzzer" -> listener?.onBuzzerReceived(json.getBoolean("value"))

//                "actuators/buzzer/shutdown" -> listener?.onBuzzerShutdownReceived(json.getBoolean("value"))
//                "actuators/fan" -> listener?.onFanReceived(json.getBoolean("value"), if(json.isNull("shutdown")) null else json.getBoolean("shutdown") )

                "actuators/led_red" -> listener?.onLedRedReceived(json.getBoolean("value"))
                "actuators/led_green" -> listener?.onLedGreenReceived(json.getBoolean("value"))
//                "actuators/led_blue" -> listener?.onLedBlueReceived(json.getBoolean("value"))
            }
        } catch (e: Exception) {
            Log.e("AWSIoTManager", "Error parsing message", e)
        }
    }

    @SuppressLint("DefaultLocale")
    fun publish(value: Any, topic: String) {
        var message = "{\"value\": $value}"

        Log.i("iot", "publish: $topic")

        if (value is Float) {
            val roundedValue = String.format("%.2f", value).toFloat()
            message = "{\"value\": ${roundedValue}}"
        }
        mqttManager.publishString(message, topic, AWSIotMqttQos.QOS0)
    }

    fun disconnect() { mqttManager.disconnect() }
}