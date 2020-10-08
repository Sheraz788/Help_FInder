package com.example.helpfinders.provider.ui.chat

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsride.R
import com.example.helpfinders.model.ChatMessage
import com.example.helpfinders.model.User
import com.example.helpfinders.provider.ui.chat.adapters.LatestMessageItem
import com.example.helpfinders.provider.ui.home.HomeFragment
import com.example.helpfinders.utils.GenerateNotifications
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {

    private lateinit var galleryViewModel: ChatViewModel
    lateinit var firebaseAuth: FirebaseAuth
    var adapter = GroupAdapter<ViewHolder>()

    companion object{
        var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
        lateinit var firebaseeReference: DatabaseReference
        fun listenForLatestMessages(activity : Activity?){
            var fromId = FirebaseAuth.getInstance().uid

            firebaseeReference = firebaseDatabase.getReference("latest-messages/$fromId")

            firebaseeReference.addChildEventListener(object : ChildEventListener{
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
//                    var chatMessage = p0.getValue(ChatMessage::class.java) ?: return
//                    if(chatMessage.isReceiving) {
//                        GenerateNotifications.generateNotification(
//                            activity!!,
//                            "${HomeFragment.currentUser?.firstName} ${HomeFragment.currentUser?.lastName}",
//                            chatMessage.message,
//                            "chat"
//                        )
//                    }
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    var chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                    generateNotifications(chatMessage, activity)
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

        }

        fun generateNotifications(chatMessage : ChatMessage, activity: Activity?){

            var firebaseRef = FirebaseDatabase.getInstance().getReference("/users/${chatMessage.fromId}")


            firebaseRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var user = p0.getValue(User::class.java)
                    if(chatMessage.isReceiving) {
                        GenerateNotifications.generateNotification(
                            activity!!,
                            "${user?.firstName} ${user?.lastName}",
                            chatMessage.message,
                            "chat"
                        )
                    }
                }

            })


        }
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
                ViewModelProviders.of(this).get(ChatViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_chat, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        firebaseeReference = firebaseDatabase.reference

        recyclerview_chat_list.layoutManager = LinearLayoutManager(activity)
        recyclerview_chat_list.adapter = adapter
        recyclerview_chat_list.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
        listenForLatestMessages(
            activity
        )
        listenForLatestMessage()
        setUpEventListeners()
    }

    var latestMessagesMap = HashMap<String, ChatMessage>()


    private fun listenForLatestMessage(){
        var fromId = FirebaseAuth.getInstance().uid

        firebaseeReference = firebaseDatabase.getReference("latest-messages/$fromId")

        firebaseeReference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                var chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {chatMessage->
            if(activity != null){
                adapter.add(
                    LatestMessageItem(
                        chatMessage,
                        activity!!
                    )
                )
            }
        }
    }

    private fun setUpEventListeners(){
         adapter.setOnItemClickListener { item, view ->

             var latestMessageItem = item as LatestMessageItem
             var bundle = bundleOf("user" to latestMessageItem.chatPartnerUser)
             Navigation.findNavController(view).navigate(R.id.action_nav_chat_to_nav_chatLogFragment, bundle)
         }
    }
}
