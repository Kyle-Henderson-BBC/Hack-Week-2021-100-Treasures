package com.example.hack_week_100_treasures

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.hack_week_100_treasures.databinding.ActivityGuessBinding
import android.os.Looper




class GuessActivity : AppCompatActivity(), SensorEventListener {

    var rotationSensor: Sensor? = null
    var gyroSensor: Sensor? = null
    var sensorReactionEnabled = true
    var rotationThresholdReached = false

    lateinit var sensorManager: SensorManager
    lateinit var binding: ActivityGuessBinding

    private val repo = CharactersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGuessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val player = Player("player 1", 0)

        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.textView.text = "seconds remaining: " + millisUntilFinished / 1000
            }

            override fun onFinish() {
                binding.textView.text = "done!"
                binding.resetButton.visibility = View.VISIBLE
                val str = "${player.name} got ${player.score} points"
                binding.textView2.text = str
                sensorManager.unregisterListener(this@GuessActivity)
            }
        }

        timer.start()

        binding.resetButton.setOnClickListener{
            //it.visibility = View.GONE
            sensorManager.registerListener(this, rotationSensor, 1000)
            sensorManager.registerListener(this, gyroSensor, 1000)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.textView2.text = repo.getRandomString()
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
            //Log.e("Value", "x: $xRotation")
            //binding.textView2.text = xRotation.toString()

            when{
                xRotation >= -0.2 -> {
                    rotationThresholdReached = true
                }
                xRotation <= -0.8 -> {
                    rotationThresholdReached = true
                }
                else -> {
                    rotationThresholdReached = false
                }
            }
        }

        if(event != null && event.sensor.type == Sensor.TYPE_GYROSCOPE){
            val xAxis = event.values[1]
            //binding.textView2.text = xAxis.toString()
            when{
                xAxis <= -1 -> {
                    if(sensorReactionEnabled) {
                        Log.d("Gyro", "Rotation Threshold Reached: $rotationThresholdReached")
                        sensorReactionEnabled = false
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({ sensorReactionEnabled = true }, 1000)

                        Log.d("Gyro", "Flicked Forward")
//                        binding.textView2.text = "Up"
                    }
                }
                xAxis >= 1 -> {
                    if(sensorReactionEnabled) {
                        Log.d("Gyro", "Rotation Threshold Reached: $rotationThresholdReached")
                        sensorReactionEnabled = false
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({ sensorReactionEnabled = true }, 1000)

                        Log.d("Gyro", "Flicked Back")
//                        binding.textView2.text = "Down"
                    }
                }
                else -> {
//                    binding.textView2.text = "Flat"
                }
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e("Changed:", sensor.toString())
    }
}