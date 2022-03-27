package it.petrolinim.eventsorganizer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import it.petrolinim.eventsorganizer.EventsOrganizerDbHelper
import it.petrolinim.eventsorganizer.R
import it.petrolinim.eventsorganizer.adapters.FriendAdapter
import kotlinx.android.synthetic.main.friends_list.*

class Friends: AppCompatActivity() {
    private lateinit var friendAdapter: FriendAdapter
    private val dbHelper: EventsOrganizerDbHelper = EventsOrganizerDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_list)
        friendAdapter = FriendAdapter(dbHelper.getUsers())

        rvFriends.adapter = friendAdapter
        rvFriends.layoutManager = LinearLayoutManager(this)

        btnAddFriend.setOnClickListener{startActivity(Intent(this, FriendEditor::class.java))}
    }
}