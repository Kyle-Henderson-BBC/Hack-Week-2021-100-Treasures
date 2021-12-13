package com.example.hack_week_100_treasures

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.hack_week_100_treasures.databinding.ActivityGuessBinding

class GuessActivity : AppCompatActivity(), SensorEventListener {

    var rotationSensor: Sensor? = null
    var gyroSensor: Sensor? = null

    lateinit var sensorManager: SensorManager
    lateinit var binding: ActivityGuessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGuessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textView.text = "seconds remaining: " + millisUntilFinished / 1000
            }

            override fun onFinish() {
                binding.textView.text = "done!"
                binding.resetButton.visibility = View.VISIBLE
            }
        }

        timer.start()

        binding.resetButton.setOnClickListener{
            it.visibility = View.GONE
            timer.start()
        }
    }

    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(this, rotationSensor, 1000)
        sensorManager.registerListener(this, gyroSensor, 1000)
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val xRotation = event.values[0]
            Log.e("Value", "x: $xRotation")
            //binding.textView2.text = xRotation.toString()

            when{
                xRotation <= 0.3 -> binding.textView2.text = "Up"
                xRotation >= 0.6 -> binding.textView2.text = "Down"
                else -> binding.textView2.text = "Flat"
            }
        }

//        if(event != null && event.sensor.type == Sensor.TYPE_GYROSCOPE){
//            val xAxis = event.values[1]
//            //binding.textView2.text = xAxis.toString()
//            when{
//                xAxis <= -0.5 -> binding.textView2.text = "Up"
//                xAxis >= 0.5 -> binding.textView2.text = "Down"
//                else -> binding.textView2.text = "Flat"
//            }
//        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e("Changed:", sensor.toString())
    }
}