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
import android.view.MotionEvent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


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
    private var charactersList: MutableList<Character> = mutableListOf()

    private val model: MainViewModel by viewModels()

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
        binding.yourFaves?.settingsButton?.visibility = View.GONE
        binding.yourFaves?.startButton?.visibility = View.GONE

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
                binding.guessLayout?.passesButton?.visibility = if(charactersList.size > 0) View.VISIBLE else View.GONE

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
                player.score = 0
                charactersList = mutableListOf()
                timer.start()
                binding.guessLayout?.constraint?.setBackgroundColor(getColor(R.color.blue))
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

            binding.guessLayout?.constraint?.setBackgroundColor(getColor(R.color.blue))
            countDown.start()
        }

        binding.guessLayout?.passesButton?.setOnClickListener{
            binding.guessLayout?.root?.visibility = View.GONE
            binding.yourFaves?.root?.visibility = View.VISIBLE

            binding.yourFaves?.mainTitle?.text = charactersList[0].name
            binding.yourFaves?.subTitle?.text = charactersList[0].desc

            binding.yourFaves?.buttonLinearLayout?.visibility = View.VISIBLE

            setUpRecycler()
        }

        binding.yourFaves?.scrollSidePanelDownButton?.setOnClickListener{
            scrollDown()
        }

        binding.yourFaves?.scrollSidePanelUpButton?.setOnClickListener{
            scrollUp()
        }

        binding.yourFaves?.endButton?.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.yourFaves?.passButton?.setOnClickListener {
            binding.guessLayout?.root?.visibility = View.VISIBLE
            binding.yourFaves?.root?.visibility = View.GONE

            binding.guessLayout?.panelTitle?.text = getString(R.string.count_down)
            binding.guessLayout?.characterView?.text = getString(R.string.ready)
            binding.guessLayout?.buttonLinearLayout?.visibility = View.GONE
            countDown.start()
        }
    }

    private fun setUpRecycler() {
        val adapter = GuessAdapter(charactersList)
        binding.yourFaves?.sidePanelRecyclerView?.layoutManager = LinearLayoutManager(this)
        //Disables swiping
        binding.yourFaves?.sidePanelRecyclerView?.addOnItemTouchListener(object: RecyclerView.SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return true
            }
        })

        binding.yourFaves?.sidePanelRecyclerView?.adapter = adapter

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
            current?.let { charactersList.add(it) }
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

    private fun scrollUp(){
        val max = binding.yourFaves?.sidePanelRecyclerView?.adapter?.itemCount ?: 0
        model.guessPosition -= 1
        if(model.guessPosition < 0) model.guessPosition = max - 1

        binding.yourFaves?.sidePanelRecyclerView?.smoothScrollToPosition(model.guessPosition)
        updateScreen()

    }

    private fun scrollDown(){
        val max = binding.yourFaves?.sidePanelRecyclerView?.adapter?.itemCount ?: 0
        model.guessPosition += 1
        if(model.guessPosition >= max) model.guessPosition = 0

        binding.yourFaves?.sidePanelRecyclerView?.smoothScrollToPosition(model.guessPosition)
        updateScreen()
    }

    private fun updateScreen(){
        val character = charactersList[model.guessPosition]
        binding.yourFaves?.mainTitle?.text = character.name
        binding.yourFaves?.subTitle?.text = character.desc


    }

}