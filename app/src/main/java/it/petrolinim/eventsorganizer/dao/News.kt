package it.petrolinim.eventsorganizer.dao

import java.io.Serializable
import java.util.*

data class News(
    val eventId: String,
    val message: String,
    val date: Date
): Serializable