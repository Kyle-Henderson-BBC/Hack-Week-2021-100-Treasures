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

    private var correctSfxId: Int? = null
    private var incorrectSfxId: Int? = null
    private var clueSfxId: Int? = null
    private var current: Character? = null

    private val repo = CharactersRepository()
    private val soundPool = SoundPool.Builder().setMaxStreams(2).build()
    private val charactersMap: MutableMap<Character, Boolean> = mutableMapOf()

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

        player = Player("Times Up!", 0)
        binding.guessLayout?.buttonLinearLayout?.visibility = View.GONE

        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val str = (millisUntilFinished / 1000).toString()
                binding.guessLayout?.timeView?.text = str
            }

            override fun onFinish() {
                binding.guessLayout?.panelTitle?.text = getString(R.string.score)
                binding.guessLayout?.characterView?.text = player.name
                binding.guessLayout?.timeView?.text = player.score.toString()

                binding.guessLayout?.buttonLinearLayout?.visibility = View.VISIBLE

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
                binding.guessLayout?.panelTitle?.text = getString(R.string.time)
                timer.start()
                current = setCurrentCharacter(repo.getNextCharacterAndConsume())
            }
        }
        binding.guessLayout?.panelTitle?.text = getString(R.string.count_down)
        binding.guessLayout?.characterView?.text = getString(R.string.ready)
        countDown.start()

        binding.guessLayout?.endButton?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.guessLayout?.passButton?.setOnClickListener {
            binding.guessLayout?.panelTitle?.text = getString(R.string.count_down)
            binding.guessLayout?.characterView?.text = getString(R.string.ready)
            binding.guessLayout?.buttonLinearLayout?.visibility = View.GONE
            countDown.start()
        }

        binding.guessLayout?.passesButton?.setOnClickListener{
            binding.guessLayout?.root?.visibility = View.GONE
            binding.yourFaves?.root?.visibility = View.VISIBLE

            binding.yourFaves?.settingsButton?.visibility = View.GONE
            binding.yourFaves?.startButton?.visibility = View.GONE
            binding.yourFaves?.mainTitle?.text = charactersMap.toString()

            setUpRecycler()
        }


    }

    private fun setUpRecycler() {
        //todo
        val adapter  = MainAdapter(charactersMap.keys.map { it.name })
    }

    override fun onStop() {
        super.onStop()
        tiltEventService.stopSensing()
    }

    private fun moveOn(correct: Boolean){
        if(correct) {
            updateScore()
            charactersMap[current!!] = true
            playCorrectSound()
        } else {
            charactersMap[current!!] = false
            playIncorrectSound()
        }
        current = setCurrentCharacter(repo.getNextCharacterAndConsume())
    }

    private fun setCurrentCharacter(character: Character): Character? {
        binding.guessLayout?.characterView?.text = character.name

        character.soundClueId?.let { soundClueResId ->
            clueSfxId = soundPool.load(applicationContext, soundClueResId, 2)
            binding.root.setOnClickListener {
                clueSfxId?.let { soundPool.play(it, 1f, 1f, 10, 0, 1f) };
            }
        } ?: run {
            binding.root.setOnClickListener(null)
        }
        return character
    }

    private fun playIncorrectSound() {
        incorrectSfxId?.let { soundPool.play(it, 1f, 1f, 10, 0, 1f) }
    }

    private fun playCorrectSound() {
        correctSfxId?.let { soundPool.play(it, 1f, 1f, 10, 0, 1f) }
    }

    private fun updateScore(){
        player.score += 1
    }

}