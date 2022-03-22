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
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createQuery =
            "CREATE TABLE ${Events.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${Events.NAME} TEXT," +
                    "${Events.DESCRIPTION} TEXT," +
                    "${Events.START_DAY} LONG," +
                    "${Events.END_DAY} LONG)"

        db.execSQL(createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        val deleteQuery = "DROP TABLE IF EXISTS ${Events.TABLE_NAME}"
        db.execSQL(deleteQuery)
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

    @SuppressLint("Range")
    fun getEvents(): MutableList<Event> {
        val eventsList: ArrayList<Event> = ArrayList()
        val selectQuery = "SELECT  * FROM ${Events.TABLE_NAME}"
        val db = this.readableDatabase
        val cursor: Cursor?
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var name: String
        var description: String
        var startDay: Date
        var endDay: Date
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndex(Events.NAME))
                description = cursor.getString(cursor.getColumnIndex(Events.DESCRIPTION))
                startDay = Date(cursor.getLong(cursor.getColumnIndex(Events.START_DAY)))
                endDay = Date(cursor.getLong(cursor.getColumnIndex(Events.END_DAY)))
                val emp= Event(title = name, description = description, startDay = startDay, endDay = endDay)
                eventsList.add(emp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return eventsList
    }
}
