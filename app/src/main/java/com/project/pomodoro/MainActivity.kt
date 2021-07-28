package com.project.pomodoro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.pomodoro.databinding.ActivityMainBinding

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
            if (!binding?.timeInputEt?.text.isNullOrEmpty()) {
                val inputTime = binding?.timeInputEt?.text.toString().toInt()
                stopWatches.add(
                    Stopwatch(
                        nextId++,
                        (inputTime * 60 * 1000).toLong(),
                        (inputTime * 60 * 1000).toLong(),
                        false
                    )
                )
                watchAdapter.submitList(stopWatches.toList())
            }
        }
    }

    override fun start(id: Int) {
        /*for (watch in stopWatches) {
            if (watch.id == id)*/ changeStopwatch(id, null, true)
        /*else stop(watch.id, watch.currentMs)
    }*/
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false)
    }

    override fun reset(id: Int) {
        changeStopwatch(id, 0L, false)
    }

    override fun delete(id: Int) {
        stopWatches.remove(stopWatches.find { it.id == id })
        watchAdapter.submitList(stopWatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
        val newTimers = mutableListOf<Stopwatch>()
        stopWatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, currentMs ?: it.currentMs, it.periodMs, isStarted))
            } else {
                newTimers.add(Stopwatch(it.id, it.currentMs, it.periodMs, false))
                /*if (from == "stop") {s
                    Collections.replaceAll(newTimers.toList(), true, false)
                    //newTimers.replaceAll()
                }*/
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