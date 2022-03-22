package it.petrolinim.eventsorganizer

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
        val event = events[position]
        holder.itemView.apply {
            name.text = event.title

            val format = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            startDay.text = sdf.format(event.startDay)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}