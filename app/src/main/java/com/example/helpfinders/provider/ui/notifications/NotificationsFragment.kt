package com.example.helpfinders.provider.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsride.R
import com.example.helpfinders.model.Notification
import com.example.helpfinders.model.User
import com.example.helpfinders.provider.ui.home.HomeFragment
import com.example.helpfinders.provider.ui.notifications.adapters.NotificationsAdapter
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_notifications.*


class NotificationsFragment : Fragment() {

    var adapter = GroupAdapter<ViewHolder>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        notification_recyclerview.layoutManager = LinearLayoutManager(activity)
        notification_recyclerview.adapter = adapter
        notification_recyclerview.addItemDecoration(
            DividerItemDecoration(activity,
                DividerItemDecoration.VERTICAL)
        )

        fetchNotifications()
    }

    var notificationsHashMap = HashMap<String, Notification>()
    private fun fetchNotifications(){
        // adding notification
        var user_id = HomeFragment.currentUser!!.uid
        var notificationRef = FirebaseDatabase.getInstance().getReference("notifications/${user_id}").orderByChild("order")

        notificationRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val numberOfChildern = p0.children.count()
                updateNotifications(numberOfChildern)
            }

        })

    }

    fun updateNotifications(childrenCount : Int){

        var user_id = HomeFragment.currentUser!!.uid
        var notificationRef = FirebaseDatabase.getInstance().getReference("notifications/${user_id}").orderByChild("order")
        notificationRef.addChildEventListener(object : ChildEventListener {
            var count = 1;
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                val notification = p0.getValue(Notification::class.java) ?: return
                notificationsHashMap[p0.key!!] = notification
                updateNotificationsList()

            }
            override fun onChildRemoved(p0: DataSnapshot) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val notification = p0.getValue(Notification::class.java) ?: return
                notificationsHashMap[p0.key!!] = notification
                if (count == childrenCount) {
                    updateNotificationsList()
                }
                count++
            }
        })
    }

    fun updateNotificationsList(){
        adapter.clear()
        notificationsHashMap.values.forEach {notification ->
            val type = notification.type
            val fromId = notification.fromId
            val toId = notification.uid



                // update ui
                var userRef = FirebaseDatabase.getInstance().getReference("users/${toId}")
                userRef.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        val user = p0.getValue(User::class.java)
                        if(activity != null){
                            adapter.add(
                                NotificationsAdapter(
                                    user!!,
                                    notification,
                                    activity!!
                                )
                            )
                        }
                        if(notification.viewstatus!!) {
                            // update status
                            val updateStatusRef = FirebaseDatabase.getInstance().getReference("notifications/$toId/$fromId/viewstatus")
                            updateStatusRef.setValue(false)
                        }
                    }
                })
        }
    }

}