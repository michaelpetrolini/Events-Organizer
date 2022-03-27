package it.petrolinim.eventsorganizer.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.petrolinim.eventsorganizer.EventsOrganizerDbHelper
import it.petrolinim.eventsorganizer.R
import it.petrolinim.eventsorganizer.dao.User
import kotlinx.android.synthetic.main.friend.view.*

class FriendAdapter(
    private val friends: MutableList<User>): RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    class FriendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.friend,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[holder.adapterPosition]
        holder.itemView.apply {
            tvFriend.text = "${friend.name} ${friend.surname}"

            btnDeleteFriend.setOnClickListener{view ->
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.delete_user_message)

                builder.setPositiveButton(R.string.yes){ _, _ ->
                    val dbHelper = EventsOrganizerDbHelper(view.context)
                    dbHelper.deleteUser(friend.userId)
                    friends.removeAt(holder.adapterPosition)
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
        return friends.size
    }

}