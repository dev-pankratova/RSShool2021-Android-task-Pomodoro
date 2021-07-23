package com.project.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private var adapter = WatchAdapter()
    private var stopWatches = mutableListOf<Stopwatch>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding?.let { setContentView(it.root) }

        binding?.recycler?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = WatchAdapter()
        }

        binding.addNewStopwatchButton.setOnClickListener {
            stopWatches.add(Stopwatch(nextId, 0, false))
            adapter = submitList(stopWatches.toList())
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}