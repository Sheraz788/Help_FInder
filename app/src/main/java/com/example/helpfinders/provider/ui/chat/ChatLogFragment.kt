package com.example.helpfinders.provider.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsride.R
import com.example.helpfinders.model.ChatMessage
import com.example.helpfinders.model.Notification
import com.example.helpfinders.model.User
import com.example.helpfinders.provider.ui.chat.adapters.ChatFromItem
import com.example.helpfinders.provider.ui.chat.adapters.ChatToItem
import com.example.helpfinders.provider.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat_log.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ChatLogFragment : Fragment() {

    var toUser : User? = null
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseeReference: DatabaseReference

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toUser = arguments?.getParcelable<User>("user")

        recyclerview_chat_log.layoutManager = LinearLayoutManager(activity)
        recyclerview_chat_log.adapter = adapter

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        toUser?.let { user ->
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "${user.firstName} ${user.lastName}"

        }

        eventListeners()
        listenForMessages()
    }


    private fun eventListeners(){
        send_button_chat_log.setOnClickListener {
            performSendMessage()
        }
    }


    private fun performSendMessage(){
        var message = edittext_chat_log.text.toString()

        //setting up Ids
        val fromId = firebaseAuth.uid
        val toId = toUser?.uid

        firebaseeReference = firebaseDatabase.getReference("user-messages/$fromId/$toId").push()

        val toFirebaseReference = firebaseDatabase.getReference("user-messages/$toId/$fromId").push()

        val fromChatMessage = ChatMessage(
            firebaseeReference.key!!,
            message,
            fromId!!,
            toId!!,
            true,
            false,
            Calendar.getInstance().timeInMillis
        )

        firebaseeReference.setValue(fromChatMessage)
            .addOnSuccessListener {
                edittext_chat_log.text.clear()
                //Toast.makeText(activity, "Message Sent}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "$it", Toast.LENGTH_SHORT).show()

            }

        val toChatMessage = ChatMessage(
            firebaseeReference.key!!,
            message,
            fromId,
            toId,
            false,
            true,
            Calendar.getInstance().timeInMillis
        )

        toFirebaseReference.setValue(toChatMessage)

        //notification
        val time = Calendar.getInstance().timeInMillis
        val notification = Notification(
            toId,
            fromId,
            "message",
            false,
            time,
            "Start",
            "",
            null,
            null
        )
        val messageNotification = firebaseDatabase.getReference("notifications/$toId/$fromId")
        messageNotification.setValue(notification)

        //storing latest-messages
        val latestMessagesReference = firebaseDatabase.getReference("latest-messages/$fromId/$toId")
        latestMessagesReference.setValue(fromChatMessage)
        val latestMessagesToReference = firebaseDatabase.getReference("latest-messages/$toId/$fromId")
        latestMessagesToReference.setValue(toChatMessage)
    }


    private fun listenForMessages(){
        val fromId = firebaseAuth.uid
        val toId = toUser?.uid
        firebaseeReference = firebaseDatabase.getReference("user-messages/$fromId/$toId")
        firebaseeReference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)

                if(chatMessage != null){

                    if (chatMessage.fromId == firebaseAuth.uid){
                        val currentUser = HomeFragment.currentUser

                        adapter.add(
                            ChatToItem(
                                chatMessage.message,
                                chatMessage.timestamp,
                                currentUser!!
                            )
                        )
                    }else{
                        adapter.add(
                            ChatFromItem(
                                chatMessage.message,
                                chatMessage.timestamp,
                                toUser!!
                            )
                        )
                    }
                }
                if(activity != null)
                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })



    }

}
