package com.example.iot

interface SensorDataListener {
    fun onTemperatureReceived(value: Float)
    fun onHumidityReceived(value: Float)
    fun onLdrReceived(value: Int)
    fun onMotionReceived(detected: Boolean)
    fun onGasReceived(detected: Boolean)

    fun onBuzzerReceived(bool: Boolean)
    fun onFanReceived(bool: Boolean, shutdown: Boolean?)

    fun onLedRedReceived(bool: Boolean)
    fun onLedGreenReceived(bool: Boolean)
    fun onLedBlueReceived(bool: Boolean)
//    fun onBuzzerShutdownReceived(bool: Boolean)
}