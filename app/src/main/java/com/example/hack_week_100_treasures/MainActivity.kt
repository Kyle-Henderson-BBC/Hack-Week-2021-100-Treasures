package com.example.hack_week_100_treasures

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hack_week_100_treasures.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val model: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Hack Week"

        binding.sidePanelRecyclerView?.layoutManager = LinearLayoutManager(this)
        binding.sidePanelRecyclerView?.adapter = MainAdapter(listOf("item 1", "item 2", "item 3"))
        binding.sidePanelRecyclerView?.addOnItemTouchListener(object: RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return true
                }
            })

        binding.startButton.setOnClickListener{
            val intent = Intent(this, GuessActivity::class.java)
            startActivity(intent)
        }

        binding.settingsButton.visibility = View.GONE
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.scrollSidePanelDownButton?.setOnClickListener{
            scrollDown()
        }
        binding.scrollSidePanelUpButton?.setOnClickListener{
            scrollUp()
        }
    }

    private fun scrollUp(){
        val max = binding.sidePanelRecyclerView?.adapter?.itemCount ?: 0
        model.mainPosition += 1
        if(model.mainPosition >= max) model.mainPosition = 0

        binding.sidePanelRecyclerView?.smoothScrollToPosition(model.mainPosition)

    }
    private fun scrollDown(){
        val max = binding.sidePanelRecyclerView?.adapter?.itemCount ?: 0
        model.mainPosition -= 1
        if(model.mainPosition < 0) model.mainPosition = max - 1

        binding.sidePanelRecyclerView?.smoothScrollToPosition(model.mainPosition)
    }
}
