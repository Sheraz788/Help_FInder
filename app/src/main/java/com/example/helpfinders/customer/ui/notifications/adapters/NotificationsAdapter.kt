package com.example.helpfinders.customer.ui.notifications.adapters

import android.app.Activity
import android.graphics.Paint
import android.view.Gravity
import android.view.Window
import androidx.navigation.findNavController
import com.example.helpfinders.model.Notification
import com.example.helpfinders.model.User
import com.example.helpfinders.customer.ui.dialog.ConfirmationDialog
import com.example.helpfinders.customer.ui.dialog.RatingDialog
import com.example.helpfinders.customer.ui.dialog.PaymentReceiptDialog
import com.example.helpfinders.utils.Utils
import com.example.letsride.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.notification_layout_row.view.*

class NotificationsAdapter(var user: User, var notification: Notification, var context: Activity): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.notification_layout_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tv_status.paintFlags = viewHolder.itemView.tv_status.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        viewHolder.itemView.tv_status.text = notification.customerstatus

        if (notification.type == "message"){
            viewHolder.itemView.tv_notification_message.text = "${user.firstName} ${user.lastName} sent a message."
        }

        viewHolder.itemView.tv_notification_time.text = Utils.fromMillisToTimeString(notification.timestamp!!)

        if (user.profileImage != "")
            Picasso.get().load(user.profileImage).into(viewHolder.itemView.user_profile_image)

        //if there is rating status show payment dialog
        if(notification.customerstatus == "Rate"){

            showPaymentDetailsDialog(context, notification, user)

        }

        //showing rating pop up dialog
        viewHolder.itemView.tv_status.setOnClickListener {
            it.findNavController().popBackStack()
            if(notification.customerstatus == "Rate"){
                showRatingDialog()
            }

        }
    }

    fun showConfirmationDialog() {
        val confirmationDialog = ConfirmationDialog(context, user)
        val window1: Window = confirmationDialog.window!!
        val param1 = window1.attributes
        param1.gravity = Gravity.CENTER
        window1.attributes = param1
        window1.setBackgroundDrawableResource(R.color.transparent)
        confirmationDialog.setCanceledOnTouchOutside(false)
        confirmationDialog.show()
    }

    fun showRatingDialog() {
        val ratingDialog = RatingDialog(context, user, notification)
        val window1: Window = ratingDialog.window!!
        val param1 = window1.attributes
        param1.gravity = Gravity.CENTER
        window1.attributes = param1
        window1.setBackgroundDrawableResource(R.color.transparent)
        ratingDialog.setCanceledOnTouchOutside(false)
        ratingDialog.show()
    }

    fun showPaymentDetailsDialog(context: Activity, notification: Notification, user: User) {
        val paymentReceiptDialog = PaymentReceiptDialog(context, notification, user)
        val window1: Window = paymentReceiptDialog.window!!
        val param1 = window1.attributes
        param1.gravity = Gravity.CENTER
        window1.attributes = param1
        window1.setBackgroundDrawableResource(R.color.transparent)
        paymentReceiptDialog.setCanceledOnTouchOutside(false)
        paymentReceiptDialog.show()
    }

}