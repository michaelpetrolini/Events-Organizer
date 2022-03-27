package it.petrolinim.eventsorganizer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.petrolinim.eventsorganizer.EventsOrganizerDbHelper
import it.petrolinim.eventsorganizer.R
import it.petrolinim.eventsorganizer.dao.User
import kotlinx.android.synthetic.main.event_editor.btnSave
import kotlinx.android.synthetic.main.friend_editor.*

class FriendEditor: AppCompatActivity() {
    private val dbHelper: EventsOrganizerDbHelper = EventsOrganizerDbHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_editor)

        btnCancel.setOnClickListener{startActivity(Intent(this, Friends::class.java))}

        btnSave.setOnClickListener{
            val user = User(userId = etFriendID.text.toString(),
                            name = etFriendName.text.toString(),
                            surname = etFriendSurname.text.toString())
            dbHelper.addUser(user)
            startActivity(Intent(this, Friends::class.java))
        }
    }
}