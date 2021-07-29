package com.project.pomodoro

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.arch.lifecycle.ProcessLifecycleOwner
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.pomodoro.databinding.ActivityMainBinding
import com.project.pomodoro.utils.*

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private var binding: ActivityMainBinding? = null
    private var startTime = 0L

    private var watchAdapter = WatchAdapter(this)
    private var stopWatches = mutableListOf<Stopwatch>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startTime = System.currentTimeMillis()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

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
        changeStopwatch(id, null, true)
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
            }
        }
        watchAdapter.submitList(newTimers)
        stopWatches.clear()
        stopWatches.addAll(newTimers)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackGrounded() {
        val startIntent = Intent(this, ForegroundService::class.java)
        startIntent.putExtra(COMMAND_ID, COMMAND_START)
        startIntent.putExtra(STARTED_TIMER_TIME_MS, startTime)
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}