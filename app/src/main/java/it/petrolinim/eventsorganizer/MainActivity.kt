package it.petrolinim.eventsorganizer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var eventAdapter: EventAdapter
    private val dbHelper: EventsOrganizerDbHelper = EventsOrganizerDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        eventAdapter = EventAdapter(dbHelper.getEvents())

        rvEvents.adapter = eventAdapter
        rvEvents.layoutManager = LinearLayoutManager(this)

        addEvent.setOnClickListener{
            startActivity(Intent(this, EventEditor::class.java))
        }
    }
}