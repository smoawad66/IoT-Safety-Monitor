package com.example.iot.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.iot.SensorDataListener
import com.example.iot.aws.AWSIoTManager

class SensorViewModel(private val awsIoTManager: AWSIoTManager? = null) : ViewModel(), SensorDataListener {

    val temperature = mutableFloatStateOf(0.0f)
    val humidity = mutableFloatStateOf(0.0f)
    val ldr = mutableIntStateOf(0)
    val motion = mutableStateOf(false)
    val gas = mutableStateOf(false)

    val buzzer = mutableStateOf(false)
    val buzzerShutdown = mutableStateOf(false)

    val fan = mutableStateOf(false)
    val fanShutdown = mutableStateOf(false)

    val ledRed = mutableStateOf(false)
    val ledGreen = mutableStateOf(false)
    val ledBlue = mutableStateOf(false)

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage


    override fun onTemperatureReceived(value: Float) {
        temperature.floatValue = value
    }
    override fun onHumidityReceived(value: Float) {
        humidity.floatValue = value
    }
    override fun onLdrReceived(value: Int) {
        ldr.intValue = value
    }
    override fun onMotionReceived(value: Boolean) {
        motion.value = value
    }
    override fun onGasReceived(value: Boolean) {
        gas.value = value
    }


    override fun onBuzzerReceived(value: Boolean) {
        buzzer.value = value && !buzzerShutdown.value
//        buzzerShutdown.value = shutdown ?: buzzerShutdown.value
    }
    override fun onFanReceived(value: Boolean, shutdown: Boolean?) {
        fan.value = value && (shutdown in arrayOf(false, null)) && !fanShutdown.value
        fanShutdown.value = shutdown ?: fanShutdown.value
    }
    override fun onLedRedReceived(value: Boolean) {
        ledRed.value = value
    }
    override fun onLedGreenReceived(value: Boolean) {
        ledGreen.value = value
    }
    override fun onLedBlueReceived(value: Boolean) {
        ledGreen.value = value
    }

//    override fun onBuzzerShutdownReceived(value: Boolean) {
//        buzzerShutdown.value = value
//        buzzer.value = if (value) false else buzzer.value
//    }


    fun toggleBuzzerShutdown() {
        if (awsIoTManager?.isConnected == false) {
            _toastMessage.value = "Failed to shutdown buzzer!"
            return
        }

        if (!buzzerShutdown.value) buzzer.value = false
        buzzerShutdown.value = !buzzerShutdown.value
        awsIoTManager?.publish(buzzerShutdown.value, "actuators/buzzer/shutdown")
    }

    fun toggleFanShutdown() {
        if (awsIoTManager?.isConnected == false) {
            _toastMessage.value = "Failed to shutdown fan!"
            return
        }

        if (!fanShutdown.value) buzzer.value = false
        fanShutdown.value = !fanShutdown.value
        awsIoTManager?.publish(fanShutdown.value, "actuators/fan/shutdown")
    }
    fun toggleBlueLed() {
        if (awsIoTManager?.isConnected == false) {
            _toastMessage.value = "Failed to toggle blue led!"
            return
        }
        ledBlue.value = !ledBlue.value
        Log.i("iot", "manager: $awsIoTManager")
        awsIoTManager?.publish(ledBlue.value, "actuators/led_blue")
    }
}