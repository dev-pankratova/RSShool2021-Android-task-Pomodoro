package com.project.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.pomodoro.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), StopwatchListener {

    private var binding: ActivityMainBinding? = null

    private var watchAdapter = WatchAdapter(this)
    private var stopWatches = mutableListOf<Stopwatch>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding?.let { setContentView(it.root) }

        binding?.recycler?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = watchAdapter
        }

        binding?.addNewStopwatchButton?.setOnClickListener {
            val inputTime = binding?.timeInputEt?.text.toString()
            stopWatches.add(Stopwatch(nextId, 100, false))
            watchAdapter.submitList(stopWatches.toList())
        }
    }

    override fun start(id: Int) {
        changeStopwatch(id, null, true)
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false)
    }

    override fun reset(id: Int) {
        changeStopwatch(id, 0L, false)
    }

    override fun delete(id: Int) {
        stopWatches.remove(stopWatches.find { it.id == id})
        watchAdapter.submitList(stopWatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
        val newTimers = mutableListOf<Stopwatch>()
        stopWatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, currentMs ?: it.currentMs, isStarted))
            } else {
                newTimers.add(it)
            }
        }
        watchAdapter.submitList(newTimers)
        stopWatches.clear()
        stopWatches.addAll(newTimers)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}