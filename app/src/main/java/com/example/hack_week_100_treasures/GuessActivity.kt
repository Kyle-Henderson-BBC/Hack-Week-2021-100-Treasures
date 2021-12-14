package com.example.hack_week_100_treasures

import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.hack_week_100_treasures.databinding.ActivityGuessBinding
import com.example.hack_week_100_treasures.sensor.TiltEventService
import android.media.SoundPool


class GuessActivity : AppCompatActivity() {
    lateinit var tiltEventService: TiltEventService
    lateinit var binding: ActivityGuessBinding
    lateinit var player: Player
    var correctSfxId: Int? = null
    var incorrectSfxId: Int? = null

    private val repo = CharactersRepository()
    private val soundPool = SoundPool.Builder().setMaxStreams(2).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        correctSfxId = soundPool.load(applicationContext, R.raw.correct_sfx, 1)
        incorrectSfxId = soundPool.load(applicationContext, R.raw.incorrect_sfx, 1)

        binding = ActivityGuessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tiltEventService = TiltEventService(
            getSystemService(Context.SENSOR_SERVICE) as SensorManager,
            { moveOn(false) },
            { moveOn(true) }
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
        if(correct) {
            updateScore()
            playCorrectSound()
        } else {
            playIncorrectSound()
        }
        binding.character.text = repo.getRandomString()
    }

    private fun playIncorrectSound() {
        incorrectSfxId?.let { soundPool.play(it, 1f, 1f, 10, 0, 1f) };
    }

    private fun playCorrectSound() {
        correctSfxId?.let { soundPool.play(it, 1f, 1f, 10, 0, 1f) };
    }

    private fun updateScore(){
        player.score += 1
        val str = "Score: " + player.score.toString()
        binding.score.text = str
    }

}