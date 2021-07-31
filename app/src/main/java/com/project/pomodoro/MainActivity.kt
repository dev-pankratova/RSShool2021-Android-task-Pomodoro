package com.project.pomodoro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.pomodoro.databinding.ActivityMainBinding
import com.project.pomodoro.utils.COMMAND_ID
import com.project.pomodoro.utils.COMMAND_START
import com.project.pomodoro.utils.COMMAND_STOP
import com.project.pomodoro.utils.ForegroundService

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private var binding: ActivityMainBinding? = null

    private var watchAdapter = WatchAdapter(this)
    private var stopWatches = mutableListOf<Stopwatch>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding?.let { setContentView(it.root) }

        clearFocusEditTxt()
        initAdapter()
        addConstraintsToInput()

        binding?.addNewStopwatchButton?.setOnClickListener {
            if (!binding?.hourInputEt?.text.isNullOrEmpty() || !binding?.minuteInputEt?.text.isNullOrEmpty()) {
                var inputHours: Int? = null
                var inputMinutes: Int? = null
                var commonTimeInMilli: Long? = null

                if (!binding?.hourInputEt?.text.isNullOrEmpty()) inputHours =
                    binding?.hourInputEt?.text.toString().toInt()
                if (!binding?.minuteInputEt?.text.isNullOrEmpty()) inputMinutes =
                    binding?.minuteInputEt?.text.toString().toInt()

                if (inputHours != null && inputMinutes != null) commonTimeInMilli =
                    (inputHours * 3600 * 1000 + inputMinutes * 60 * 1000).toLong()
                else if (inputHours != null && inputMinutes == null) commonTimeInMilli =
                    (inputHours * 3600 * 1000).toLong()
                else if (inputHours == null && inputMinutes != null) commonTimeInMilli =
                    (inputMinutes * 60 * 1000).toLong()

                stopWatches.add(
                    Stopwatch(
                        nextId++,
                        commonTimeInMilli,
                        commonTimeInMilli,
                        false
                    )
                )
                watchAdapter.submitList(stopWatches.toList())
                clearEditTxts()
            }
        }
    }

    private fun initAdapter() {
        binding?.recycler?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = watchAdapter
        }
    }

    private fun addConstraintsToInput() {
        binding?.hourInputEt?.doAfterTextChanged {
            if (!binding?.hourInputEt?.text.isNullOrEmpty()) {
                if (binding?.hourInputEt?.text.toString()
                        .toInt() >= 25
                ) binding?.hourInputEt?.setText("")
            }
        }

        binding?.minuteInputEt?.doAfterTextChanged {
            if (!binding?.minuteInputEt?.text.isNullOrEmpty()) {
                if (binding?.minuteInputEt?.text.toString()
                        .toInt() >= 60
                ) binding?.minuteInputEt?.setText("")
            }
        }
    }

    override fun start(id: Int) {
        clearEditTxts()
        changeStopwatch(id, null, true)
    }

    override fun stop(id: Int, currentMs: Long) {
        clearEditTxts()
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
        startService(startIntent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    private fun clearFocusEditTxt() {
        binding?.root?.setOnClickListener {
            clearEditTxts()
        }
    }

    private fun clearEditTxts() {
        binding?.hourInputEt?.clearFocus()
        binding?.minuteInputEt?.clearFocus()
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}