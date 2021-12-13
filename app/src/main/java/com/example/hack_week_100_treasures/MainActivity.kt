package com.example.hack_week_100_treasures

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.example.hack_week_100_treasures.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}