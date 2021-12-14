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


        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val str = (millisUntilFinished / 1000).toString()
                binding.guessLayout?.timeView?.text = str
            }

            override fun onFinish() {
                binding.guessLayout?.panelTitle?.text = "Score:"
                binding.guessLayout?.characterView?.text = player.name
                binding.guessLayout?.timeView?.text = player.score.toString()
                tiltEventService.stopSensing()
            }
        }

        val countDown = object: CountDownTimer(4000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                val str = (millisUntilFinished / 1000).toString()
                binding.guessLayout?.timeView?.text = str
            }

            override fun onFinish() {
                tiltEventService.startSensing()
                binding.guessLayout?.panelTitle?.text = "Time:"
                timer.start()
                binding.guessLayout?.characterView?.text = repo.getRandomString()
            }
        }
        binding.guessLayout?.panelTitle?.text = "CountDown:"
        binding.guessLayout?.characterView?.text = "Ready!"
        countDown.start()
//        binding.resetButton.setOnClickListener{
//            sensorManager.registerListener(this, rotationSensor, 1000)
//            sensorManager.registerListener(this, gyroSensor, 1000)
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.floatingActionButton.setOnClickListener{
//            moveOn(false)
//        }
//        binding.floatingActionButton2.setOnClickListener{
//            moveOn(true)
//        }


    }
    override fun onStart() {
        super.onStart()
        //tiltEventService.startSensing()
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
        binding.guessLayout?.characterView?.text = repo.getRandomString()
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
        //binding.score.text = str
    }

}