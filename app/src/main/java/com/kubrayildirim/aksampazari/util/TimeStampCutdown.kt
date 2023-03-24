package com.kubrayildirim.aksampazari.util

    fun cutDownTime(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val remainingTime = timestamp - currentTime

        val seconds = (remainingTime / 1000) % 60
        val minutes = (remainingTime / (1000 * 60)) % 60
        val hours = (remainingTime / (1000 * 60 * 60)) % 24
        val days = (remainingTime / (1000 * 60 * 60 * 24))

        return if (days > 0) {
            "$days gün $hours saat $minutes dk"
        } else if (hours > 0) {
            "$hours saat $minutes dk"
        } else if (minutes > 0) {
            "$minutes dk"
        } else {
            "$seconds sn"
        }

    }