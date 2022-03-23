package it.petrolinim.eventsorganizer

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import java.util.*
import kotlin.collections.ArrayList

class EventsOrganizerDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    object Events: BaseColumns {
        const val TABLE_NAME = "events"
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val START_DAY = "startDay"
        const val END_DAY = "endDay"
        const val CREATE_QUERY = "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$NAME TEXT," +
                "$DESCRIPTION TEXT," +
                "$START_DAY LONG," +
                "$END_DAY LONG)"
        const val DROP_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SELECT_QUERY = "SELECT  * FROM $TABLE_NAME ORDER BY $START_DAY"
        const val DELETE_QUERY = "DELETE FROM $TABLE_NAME WHERE ${BaseColumns._ID} = ?"
    }

    object EventNews: BaseColumns {
        const val TABLE_NAME = "eventNews"
        const val EVENT_ID = "eventID"
        const val MESSAGE = "message"
        const val DATE = "date"
        const val CREATE_QUERY = "CREATE TABLE $TABLE_NAME (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "$EVENT_ID INTEGER," +
                "$MESSAGE TEXT," +
                "$DATE LONG)"
        const val DROP_QUERY = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SELECT_QUERY = "SELECT * FROM $TABLE_NAME WHERE $EVENT_ID = ? ORDER BY $DATE"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Events.CREATE_QUERY)
        db.execSQL(EventNews.CREATE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Events.DROP_QUERY)
        db.execSQL(EventNews.DROP_QUERY)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "EventsOrganizer.db"
    }

    fun addEvent(event: Event): Long {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(Events.NAME, event.title)
        cv.put(Events.DESCRIPTION, event.description)
        cv.put(Events.START_DAY, event.startDay.time)
        cv.put(Events.END_DAY, event.endDay.time)

        val result = db.insert(Events.TABLE_NAME, null, cv)
        db.close()
        return result
    }

    fun deleteEvent(id: Long?): Int {
        if (id == null){
            throw Exception("Event ID not set.")
        }

        val db = this.writableDatabase
        val statement = db.compileStatement(Events.DELETE_QUERY)
        statement.bindLong(1, id)
        val result = statement.executeUpdateDelete()
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
        var id: Long
        var name: String
        var description: String
        var startDay: Date
        var endDay: Date
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                name = cursor.getString(cursor.getColumnIndex(Events.NAME))
                description = cursor.getString(cursor.getColumnIndex(Events.DESCRIPTION))
                startDay = Date(cursor.getLong(cursor.getColumnIndex(Events.START_DAY)))
                endDay = Date(cursor.getLong(cursor.getColumnIndex(Events.END_DAY)))
                val emp= Event(id = id, title = name, description = description, startDay = startDay, endDay = endDay)
                eventsList.add(emp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return eventsList
    }
}
