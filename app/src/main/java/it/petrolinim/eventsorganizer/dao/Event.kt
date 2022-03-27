package it.petrolinim.eventsorganizer.dao

import java.io.Serializable
import java.util.*

data class Event(
        val eventId: String? = null,
        val title: String,
        val description: String,
        val startDay: Date,
        val endDay: Date,
        val participants: List<User>,
        val news: List<News>
): Serializable