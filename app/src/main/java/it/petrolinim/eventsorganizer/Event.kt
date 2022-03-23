package it.petrolinim.eventsorganizer

import java.util.*

data class Event(
        val id: Long? = null,
        val title: String,
        val description: String,
        val startDay: Date,
        val endDay: Date
        )