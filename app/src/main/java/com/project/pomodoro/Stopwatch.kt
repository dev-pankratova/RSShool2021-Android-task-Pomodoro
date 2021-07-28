package com.project.pomodoro

data class Stopwatch(
    val id: Int,
    var currentMs: Long?,
    var periodMs: Long?,
    var isStarted: Boolean,
    var backgroundColor: Int? = null
)