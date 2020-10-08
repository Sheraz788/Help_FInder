package com.example.helpfinders.provider.ui.chat.adapters

import android.app.Activity
import com.example.letsride.R
import com.example.helpfinders.model.ChatMessage
import com.example.helpfinders.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_users_message_row.view.*

class LatestMessageItem(var chatMessage: ChatMessage, var activity : Activity) : Item<ViewHolder>(){
    var chatPartnerUser : User? = null

        override fun getLayout(): Int {
            return R.layout.latest_users_message_row
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val chatPartnerId : String
            chatPartnerId = if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatMessage.toId
            }else{
                chatMessage.fromId
            }

        val databaseReference =FirebaseDatabase.getInstance().getReference("users/$chatPartnerId")
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    chatPartnerUser = p0.getValue(User::class.java)
                    viewHolder.itemView.username_latest_message.text = "${chatPartnerUser?.firstName} ${chatPartnerUser?.lastName}"
                    val imageUri = chatPartnerUser?.profileImage
                    if(imageUri == "" || imageUri == null){
                        viewHolder.itemView.imageview_latest_message.setImageResource(R.drawable.img_user_profile)
                    }else{
                        Picasso.get().load(chatPartnerUser?.profileImage).into(viewHolder.itemView.imageview_latest_message)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })


        viewHolder.itemView.message_textview_latest_message.text = chatMessage.message
    }

}