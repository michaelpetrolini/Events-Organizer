package it.petrolinim.eventsorganizer.dao

import java.io.Serializable

data class User (
    val userId: String,
    val name: String,
    val surname: String,
    var isSelected: Boolean = false
): Serializable {
    override fun equals(other: Any?): Boolean {
        return other is User
                && userId == other.userId
                && name == other.name
                && surname == other.surname
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        return result
    }
}