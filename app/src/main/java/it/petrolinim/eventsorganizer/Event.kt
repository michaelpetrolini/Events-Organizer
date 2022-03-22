package it.petrolinim.eventsorganizer

import java.util.*

data class Event(
        val title: String,
        val description: String,
        val startDay: Date,
        val endDay: Date
        )