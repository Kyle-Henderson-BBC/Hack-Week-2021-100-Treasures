package com.example.hack_week_100_treasures.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlin.math.roundToInt
import kotlin.math.sqrt

class TiltEventService(
    private val sensorManager: SensorManager,
    private val onTiltUp: () -> Unit,
    private val onTiltDown: () -> Unit): SensorEventListener {

    private var rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private var gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private var magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private var accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var sensorReactionEnabled = true

    fun startSensing() {
        sensorManager.registerListener(this, rotationSensor, 1000)
        sensorManager.registerListener(this, gyroSensor, 1000)
        sensorManager.registerListener(this, magneticFieldSensor, 1000)
        sensorManager.registerListener(this, accelerometerSensor, 1000)
    }

    fun stopSensing() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            if(sensorReactionEnabled) {
                val gravity = event.values

                val norm_Of_g = sqrt(
                    (gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]).toDouble()
                )

                // Normalize the accelerometer vector
                gravity[0] = (gravity[0] / norm_Of_g).toFloat()
                gravity[1] = (gravity[1] / norm_Of_g).toFloat()
                gravity[2] = (gravity[2] / norm_Of_g).toFloat()

                val inclination = Math.toDegrees(Math.acos(gravity.get(2).toDouble())).roundToInt()
                Log.d("Inclination", inclination.toString())

                if(inclination in 20..50) {
                    disableSensorFor1Second()
                    Log.d("TiltEventService", "tilted back")
                    onTiltUp()
                } else if(inclination in 140..170) {
                    disableSensorFor1Second()
                    Log.d("TiltEventService", "tiled forward")
                    onTiltDown()
                }
            }
        }
    }

    private fun disableSensorFor1Second() {
        sensorReactionEnabled = false
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ sensorReactionEnabled = true }, 1000)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e("Changed:", sensor.toString())
    }
}