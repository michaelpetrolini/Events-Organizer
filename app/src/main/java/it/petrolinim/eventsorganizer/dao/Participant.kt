package it.petrolinim.eventsorganizer.dao

data class Participant (
    val userId: String,
    val eventId: String,
    val response: String,
    val message: String
)