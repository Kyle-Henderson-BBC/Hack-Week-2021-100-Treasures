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
import com.example.hack_week_100_treasures.sensor.TiltEventService
import kotlin.math.roundToInt
import kotlin.math.sqrt


class GuessActivity : AppCompatActivity() {
    lateinit var tiltEventService: TiltEventService
    lateinit var binding: ActivityGuessBinding
    lateinit var player: Player

    private val repo = CharactersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGuessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tiltEventService = TiltEventService(
            getSystemService(Context.SENSOR_SERVICE) as SensorManager,
            {},
            {}
        )

        player = Player("player 1", 0)

        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val str = "seconds remaining: " + millisUntilFinished / 1000
                binding.time.text = str
            }

            override fun onFinish() {
                binding.time.text = getString(R.string.TimerDone)
                binding.resetButton.visibility = View.VISIBLE

                val str = "${player.name} got ${player.score} points"
                binding.character.text = str

                tiltEventService.stopSensing()
            }
        }

        timer.start()

        binding.resetButton.setOnClickListener{
            tiltEventService.startSensing()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.floatingActionButton.setOnClickListener{
            moveOn(false)
        }
        binding.floatingActionButton2.setOnClickListener{
            moveOn(true)
        }

        binding.character.text = repo.getRandomString()
    }

    override fun onStart() {
        super.onStart()
        tiltEventService.startSensing()
    }

    override fun onStop() {
        super.onStop()
        tiltEventService.stopSensing()
    }



    private fun moveOn(correct: Boolean){
        if(correct) updateScore()
        binding.character.text = repo.getRandomString()
    }

    private fun updateScore(){
        player.score += 1
        val str = "Score: " + player.score.toString()
        binding.score.text = str
    }
}