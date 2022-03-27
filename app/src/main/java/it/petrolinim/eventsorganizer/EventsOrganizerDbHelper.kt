package it.petrolinim.eventsorganizer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import it.petrolinim.eventsorganizer.dao.Event
import it.petrolinim.eventsorganizer.dao.News
import it.petrolinim.eventsorganizer.dao.User
import java.util.*

class EventsOrganizerDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    object Events {
        const val TABLE_NAME = "events"
        const val EVENT_ID = "eventID"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val START_DAY = "startDay"
        const val END_DAY = "endDay"
        const val CREATE_QUERY = "CREATE TABLE $TABLE_NAME (" +
                "$EVENT_ID TEXT PRIMARY KEY," +
                "$NAME TEXT," +
                "$DESCRIPTION TEXT," +
                "$START_DAY LONG," +
                "$END_DAY LONG)"
        const val DROP_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SELECT_QUERY = "SELECT  * FROM $TABLE_NAME ORDER BY $START_DAY"
        const val SELECT_BY_ID_QUERY = "SELECT  * FROM $TABLE_NAME WHERE $EVENT_ID = ?"
        const val DELETE_QUERY = "DELETE FROM $TABLE_NAME WHERE $EVENT_ID = ?"
    }

    object Users: BaseColumns {
        const val TABLE_NAME = "users"
        const val USER_ID = "userID"
        const val NAME = "name"
        const val SURNAME = "surname"
        const val CREATE_QUERY = "CREATE TABLE $TABLE_NAME (" +
                "$USER_ID TEXT PRIMARY KEY," +
                "$NAME TEXT," +
                "$SURNAME TEXT)"
        const val DROP_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SELECT_QUERY = "SELECT * FROM $TABLE_NAME ORDER BY $NAME, $SURNAME"
        const val DELETE_QUERY = "DELETE FROM $TABLE_NAME WHERE $USER_ID = ?"
    }

    object EventNews: BaseColumns {
        const val TABLE_NAME = "eventNews"
        const val EVENT_ID = "eventID"
        const val MESSAGE = "message"
        const val DATE = "date"
        const val CREATE_QUERY = "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$EVENT_ID TEXT," +
                "$MESSAGE TEXT," +
                "$DATE LONG," +
                "FOREIGN KEY ($EVENT_ID) REFERENCES ${Events.TABLE_NAME}(${Events.EVENT_ID}))"
        const val DROP_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SELECT_QUERY = "SELECT * FROM $TABLE_NAME WHERE $EVENT_ID = ? ORDER BY $DATE"
        const val DELETE_QUERY = "DELETE FROM $TABLE_NAME WHERE $EVENT_ID = ?"
    }

    object Participants {
        const val TABLE_NAME = "participants"
        const val EVENT_ID = "eventID"
        const val USER_ID = "userID"
        const val RESPONSE = "response"
        const val MESSAGE = "message"
        const val CREATE_QUERY = "CREATE TABLE $TABLE_NAME (" +
                "$EVENT_ID TEXT," +
                "$USER_ID TEXT," +
                "$RESPONSE TEXT," +
                "$MESSAGE TEXT," +
                "PRIMARY KEY ($USER_ID, $EVENT_ID)," +
                "FOREIGN KEY ($EVENT_ID) REFERENCES ${Events.TABLE_NAME}(${Events.EVENT_ID})," +
                "FOREIGN KEY ($USER_ID) REFERENCES ${Users.TABLE_NAME}(${Users.USER_ID}))"
        const val DROP_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SELECT_QUERY = "SELECT * FROM ${Users.TABLE_NAME} INNER JOIN $TABLE_NAME " +
                "ON ${Users.TABLE_NAME}.${Users.USER_ID} = $TABLE_NAME.$USER_ID WHERE $EVENT_ID = ?"
        const val DELETE_PARTICIPANT = "DELETE FROM $TABLE_NAME WHERE $USER_ID = ? AND $EVENT_ID = ?"
        const val DELETE_BY_EVENT = "DELETE FROM $TABLE_NAME WHERE $EVENT_ID = ?"
        const val DELETE_BY_USER = "DELETE FROM $TABLE_NAME WHERE $USER_ID = ?"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Events.CREATE_QUERY)
        db.execSQL(Users.CREATE_QUERY)
        db.execSQL(EventNews.CREATE_QUERY)
        db.execSQL(Participants.CREATE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Events.DROP_QUERY)
        db.execSQL(Users.DROP_QUERY)
        db.execSQL(EventNews.DROP_QUERY)
        db.execSQL(Participants.DROP_QUERY)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "EventsOrganizer.db"
    }

    private fun getEventId(): String {
        val db = this.readableDatabase
        var eventId = UUID.randomUUID().toString()
        var cursor= db.rawQuery(Events.SELECT_BY_ID_QUERY, arrayOf(eventId))
        while (cursor.moveToFirst()) {
            eventId = UUID.randomUUID().toString()
            cursor= db.rawQuery(Events.SELECT_BY_ID_QUERY, arrayOf(eventId))
        }
        cursor.close()
        db.close()
        return eventId
    }

    fun addEvent(event: Event): Long {
        val eventId = getEventId()
        val cv = ContentValues()

        cv.put(Events.EVENT_ID, eventId)
        cv.put(Events.NAME, event.title)
        cv.put(Events.DESCRIPTION, event.description)
        cv.put(Events.START_DAY, event.startDay.time)
        cv.put(Events.END_DAY, event.endDay.time)

        val db = this.writableDatabase
        val result = db.insert(Events.TABLE_NAME, null, cv)

        Log.d("TAG", event.participants.toString())

        for (user: User in event.participants) {
            addParticipant(user, eventId, "asd", "asda")
        }
        db.close()
        return result
    }

    fun updateEvent(event: Event): Int {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(Events.NAME, event.title)
        cv.put(Events.DESCRIPTION, event.description)
        cv.put(Events.START_DAY, event.startDay.time)
        cv.put(Events.END_DAY, event.endDay.time)

        val result = db.update(Events.TABLE_NAME, cv,
            "${Events.EVENT_ID}=?", arrayOf(event.eventId))
        db.close()
        return result
    }

    fun deleteEvent(eventId: String?): Int {
        if (eventId == null){
            throw Exception("Event ID not set.")
        }

        val db = this.writableDatabase
        val deleteParticipantsStatement = db.compileStatement(Participants.DELETE_BY_EVENT)
        deleteParticipantsStatement.bindString(1, eventId)
        deleteParticipantsStatement.executeUpdateDelete()
        val deleteEventStatement = db.compileStatement(Events.DELETE_QUERY)
        deleteEventStatement.bindString(1, eventId)
        val result = deleteEventStatement.executeUpdateDelete()
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getEvents(): MutableList<Event> {
        val eventsList: ArrayList<Event> = ArrayList()
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(Events.SELECT_QUERY, null)
        }catch (e: SQLiteException) {
            db.execSQL(Events.SELECT_QUERY)
            return ArrayList()
        }
        var eventId: String
        var name: String
        var description: String
        var startDay: Date
        var endDay: Date
        if (cursor.moveToFirst()) {
            do {
                eventId = cursor.getString(cursor.getColumnIndex(Events.EVENT_ID))
                name = cursor.getString(cursor.getColumnIndex(Events.NAME))
                description = cursor.getString(cursor.getColumnIndex(Events.DESCRIPTION))
                startDay = Date(cursor.getLong(cursor.getColumnIndex(Events.START_DAY)))
                endDay = Date(cursor.getLong(cursor.getColumnIndex(Events.END_DAY)))
                val participants = getParticipants(eventId)
                val news = getNews(eventId)
                val event= Event(eventId = eventId,
                                title = name,
                                description = description,
                                startDay = startDay,
                                endDay = endDay,
                                participants = participants,
                                news = news)
                eventsList.add(event)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return eventsList
    }

    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(Users.USER_ID, user.userId)
        cv.put(Users.NAME, user.name)
        cv.put(Users.SURNAME, user.surname)

        val result = db.insert(Users.TABLE_NAME, null, cv)
        db.close()
        return result
    }

    fun deleteUser(userId: String?): Int {
        if (userId == null){
            throw Exception("User ID not set.")
        }

        val db = this.writableDatabase
        val deleteParticipantsStatement = db.compileStatement(Participants.DELETE_BY_USER)
        deleteParticipantsStatement.bindString(1, userId)
        deleteParticipantsStatement.executeUpdateDelete()

        val statement = db.compileStatement(Users.DELETE_QUERY)
        statement.bindString(1, userId)
        val result = statement.executeUpdateDelete()
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getUsers(): MutableList<User> {
        val usersList: ArrayList<User> = ArrayList()
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(Users.SELECT_QUERY, null)
        }catch (e: SQLiteException) {
            db.execSQL(Users.SELECT_QUERY)
            return ArrayList()
        }
        var userId: String
        var name: String
        var surname: String
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getString(cursor.getColumnIndex(Users.USER_ID))
                name = cursor.getString(cursor.getColumnIndex(Users.NAME))
                surname = cursor.getString(cursor.getColumnIndex(Users.SURNAME))
                val user= User(userId = userId, name = name, surname = surname)
                usersList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return usersList
    }

    fun addParticipant(user: User, eventId: String, response: String, message: String): Long {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(Participants.USER_ID, user.userId)
        cv.put(Participants.EVENT_ID, eventId)
        cv.put(Participants.RESPONSE, response)
        cv.put(Participants.MESSAGE, message)

        val result = db.insert(Participants.TABLE_NAME, null, cv)
        Log.d("addParticipants", result.toString())
        db.close()
        return result
    }

    fun deleteParticipant(user: User, event: Event): Int {
        val db = this.writableDatabase
        val statement = db.compileStatement(Participants.DELETE_PARTICIPANT)
        statement.bindString(1, user.userId)
        statement.bindString(2, event.eventId)
        val result = statement.executeUpdateDelete()
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getParticipants(eventId: String?): MutableList<User> {
        val participantsList: ArrayList<User> = ArrayList()
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(Participants.SELECT_QUERY, arrayOf(eventId))
        }catch (e: SQLiteException) {
            db.execSQL(Participants.SELECT_QUERY, arrayOf(eventId))
            return ArrayList()
        }
        var userId: String
        var name: String
        var surname: String
        if (cursor.moveToFirst()) {
            do {
                userId = cursor.getString(cursor.getColumnIndex(Users.USER_ID))
                name = cursor.getString(cursor.getColumnIndex(Users.NAME))
                surname = cursor.getString(cursor.getColumnIndex(Users.SURNAME))
                val user= User(userId = userId, name = name, surname = surname)
                participantsList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        Log.d("getParticipants", participantsList.toString())
        return participantsList
    }

    fun addNews(news: News): Long {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(EventNews.EVENT_ID, news.eventId)
        cv.put(EventNews.DATE, news.date.time)
        cv.put(EventNews.MESSAGE, news.message)

        val result = db.insert(EventNews.TABLE_NAME, null, cv)
        db.close()
        return result
    }

    fun deleteNews(eventId: String): Int {
        val db = this.writableDatabase
        val statement = db.compileStatement(EventNews.DELETE_QUERY)
        statement.bindString(1, eventId)
        val result = statement.executeUpdateDelete()
        db.close()
        return result
    }

    @SuppressLint("Range")
    fun getNews(eventId: String?): MutableList<News> {
        val newsList: ArrayList<News> = ArrayList()
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(EventNews.SELECT_QUERY, arrayOf(eventId))
        }catch (e: SQLiteException) {
            db.execSQL(Participants.SELECT_QUERY, arrayOf(eventId))
            return ArrayList()
        }
        var id: String
        var message: String
        var date: Date
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getString(cursor.getColumnIndex(EventNews.EVENT_ID))
                message = cursor.getString(cursor.getColumnIndex(EventNews.MESSAGE))
                date = Date(cursor.getLong(cursor.getColumnIndex(EventNews.DATE)))
                val user= News(eventId = id, message = message, date = date)
                newsList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return newsList
    }
}
