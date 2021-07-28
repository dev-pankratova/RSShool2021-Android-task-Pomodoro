package com.project.pomodoro

import android.content.res.Resources
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.project.pomodoro.databinding.WatchItemBinding

class StopwatchViewHolder(
    private val binding: WatchItemBinding,
    private val listener: StopwatchListener,
    private val resources: Resources
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.stopwatchTimer.text = stopwatch.currentMs?.displayTime()
        timeCommon = stopwatch.currentMs
        if (stopwatch.currentMs == 0L && stopwatch.periodMs == 0L) binding.watchContainer.background = AppCompatResources.getDrawable(binding.root.context, R.color.design_default_color_secondary_variant)
        else {
            stopwatch.currentMs?.let { binding.customViewTwo.setCurrent(it) }
            stopwatch.periodMs?.let { binding.customViewTwo.setPeriod(it) }

            if (stopwatch.isStarted) {
                startTimer(stopwatch)
            } else {
                stopTimer(stopwatch)
            }
        }


        initButtonsListeners(stopwatch)
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                stopwatch.currentMs?.let { it1 -> listener.stop(stopwatch.id, it1) }
                //stopwatch.periodMs?.let { it1 -> binding.customViewTwo.setPeriod(it1) }
            } else {
                listener.start(stopwatch.id)
            }
        }

        //binding.restartButton.setOnClickListener { listener.reset(stopwatch.id) }
        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }
    private var timeCommon: Long? = null
    private fun startTimer(stopwatch: Stopwatch) {
        binding.startPauseButton.text = "Stop"

        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        if (stopwatch.periodMs != 0L) timer?.start()

        if (stopwatch.backgroundColor != null) binding.watchContainer.background = AppCompatResources.getDrawable(binding.root.context,
            stopwatch.backgroundColor!!
        )
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        binding.startPauseButton.text = "Start"
        if (stopwatch.backgroundColor != null) binding.watchContainer.background = AppCompatResources.getDrawable(binding.root.context,
            stopwatch.backgroundColor!!
        )
        timer?.cancel()
        stopwatch.periodMs?.let { binding.customViewTwo.setPeriod(it) }
        stopwatch.currentMs?.let { binding.customViewTwo.setCurrent(it) }

        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }
    var current = 0L
    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(stopwatch.currentMs ?: 0, UNIT_TEN_MS) {
            val interval: Long = UNIT_TEN_MS

            override fun onTick(millisUntilFinished: Long) {

                stopwatch.currentMs = stopwatch.currentMs?.minus(interval)
                current += interval

                stopwatch.currentMs?.let { binding.customViewTwo.setCurrent(it) }
                binding.stopwatchTimer.text = stopwatch.currentMs?.displayTime()
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs?.displayTime()
                stopwatch.currentMs?.let { binding.customViewTwo.setCurrent(it) }
                binding.watchContainer.background = AppCompatResources.getDrawable(binding.root.context, R.color.design_default_color_secondary_variant)
                stopwatch.backgroundColor = R.color.design_default_color_secondary_variant
                binding.blinkingIndicator.isInvisible = true
                (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
            }

        }
    }

    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }

    private companion object {
        private const val START_TIME = "00:00:00"
        private const val UNIT_TEN_MS = 1000L
        private const val PERIOD  = 1000L * 60L * 60L * 24L // Day
    }
}