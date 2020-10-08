package com.example.helpfinders.provider.ui.chat.adapters


import com.example.letsride.R
import com.example.helpfinders.model.User
import com.example.helpfinders.utils.Utils
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*

class ChatFromItem(var message : String, var time : Long, var user : User) : Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = message
        viewHolder.itemView.tv_message_time.text = Utils.fromMillisToTimeString(time)
        var imageUri = user.profileImage
        if(imageUri == ""){
            viewHolder.itemView.imageview_chat_from_row.setImageResource(R.drawable.img_user_profile)
        }else{
            Picasso.get().load(imageUri).into(viewHolder.itemView.imageview_chat_from_row)
        }

    }

}