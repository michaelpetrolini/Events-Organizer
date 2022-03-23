package it.petrolinim.eventsorganizer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.event_editor.*
import java.text.SimpleDateFormat
import java.util.*

class EventEditor: AppCompatActivity() {

    private val startCal: Calendar = Calendar.getInstance()
    private val endCal: Calendar = Calendar.getInstance()
    private val dbHelper: EventsOrganizerDbHelper = EventsOrganizerDbHelper(this)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_editor)

        val startDay = findViewById<TextView>(R.id.etStartDay)
        startDay.showSoftInputOnFocus = false
        val startDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                startCal.set(Calendar.YEAR, year)
                startCal.set(Calendar.MONTH, monthOfYear)
                startCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDate(startDay, startCal)}

        startDay.setOnTouchListener { _, event -> popupDateForm(event, startDateSetListener, startCal)}

        val endDay = findViewById<TextView>(R.id.etEndDay)
        endDay.showSoftInputOnFocus = false
        val endDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                endCal.set(Calendar.YEAR, year)
                endCal.set(Calendar.MONTH, monthOfYear)
                endCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                updateDate(endDay, endCal)}

        endDay.setOnTouchListener{ _, event -> popupDateForm(event, endDateSetListener, endCal)}

        btnSave.setOnClickListener{
            val event = Event(
                title = etName.text.toString(),
                description = etDescription.text.toString(),
                startDay = startCal.time,
                endDay = endCal.time)
            dbHelper.addEvent(event)
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnCancel.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun popupDateForm(
        event: MotionEvent,
        dateSetListener: DatePickerDialog.OnDateSetListener,
        calendar: Calendar
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                DatePickerDialog(
                    this@EventEditor, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
        return false
    }

    private fun updateDate(tv: TextView, calendar: Calendar) {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())

        tv.text = sdf.format(calendar.time)
    }
}