package it.petrolinim.eventsorganizer

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.event.view.*
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val events: MutableList<Event>
    ): RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
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

            btnDelete.setOnClickListener{view ->
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.delete_event_message)

                builder.setPositiveButton(R.string.yes){_, _ ->
                    val dbHelper = EventsOrganizerDbHelper(view.context)
                    dbHelper.deleteEvent(event.id)
                    events.removeAt(holder.adapterPosition)
                    notifyItemRemoved(holder.adapterPosition)
                }

                builder.setNegativeButton(R.string.no){_,_ ->

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