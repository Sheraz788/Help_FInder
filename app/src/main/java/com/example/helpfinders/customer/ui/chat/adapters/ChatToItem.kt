package com.example.helpfinders.customer.ui.chat.adapters

import com.example.letsride.R
import com.example.helpfinders.model.User
import com.example.helpfinders.utils.Utils
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem(var message : String, var time : Long,  var currentUser : User) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = message
        viewHolder.itemView.tv_message_time.text = Utils.fromMillisToTimeString(time)
        var imageUri = currentUser.profileImage
        if(imageUri == ""){
            viewHolder.itemView.imageview_chat_to_row.setImageResource(R.drawable.img_user_profile)
        }else{
            Picasso.get().load(imageUri).into(viewHolder.itemView.imageview_chat_to_row)
        }
    }

}