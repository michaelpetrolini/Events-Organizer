package it.petrolinim.eventsorganizer.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import it.petrolinim.eventsorganizer.EventsOrganizerDbHelper
import it.petrolinim.eventsorganizer.R
import it.petrolinim.eventsorganizer.activities.EventEditor
import it.petrolinim.eventsorganizer.dao.Event
import kotlinx.android.synthetic.main.event.view.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(private val events: MutableList<Event>): RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        context = parent.context

        return EventViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.event,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[holder.adapterPosition]
        holder.itemView.apply {
            name.text = event.title

            val format = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            startDay.text = sdf.format(event.startDay)

            btnEditEvent.setOnClickListener{ view ->
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.edit_event_message)

                builder.setPositiveButton(R.string.yes){ _, _ ->
                    val intent = Intent(context, EventEditor::class.java)
                    intent.putExtra("event", event as Serializable)
                    startActivity(context, intent, null)
                }

                builder.setNegativeButton(R.string.no){ _, _ ->

                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

            btnDeleteEvent.setOnClickListener{ view ->
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.delete_event_message)

                builder.setPositiveButton(R.string.yes){ _, _ ->
                    val dbHelper = EventsOrganizerDbHelper(view.context)
                    dbHelper.deleteEvent(event.eventId)
                    events.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }

                builder.setNegativeButton(R.string.no){ _, _ ->

                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}