package it.petrolinim.eventsorganizer.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.petrolinim.eventsorganizer.R
import it.petrolinim.eventsorganizer.dao.User
import kotlinx.android.synthetic.main.friend.view.*

class ParticipantAdapter(
    val users: MutableList<User>, private val participants: List<User>
): RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder>() {
    class ParticipantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        users.forEach { if (participants.contains(it)) it.isSelected = true}
        return ParticipantViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.select_friend,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        val user = users[position]
        holder.itemView.apply {
            tvFriend.text = "${user.name} ${user.surname}"
            tvFriend.setBackgroundColor(if (user.isSelected) Color.CYAN else Color.WHITE)
            tvFriend.setOnClickListener{
                user.isSelected = !user.isSelected
                tvFriend.setBackgroundColor(if (user.isSelected) Color.CYAN else Color.WHITE)
            }

        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
}