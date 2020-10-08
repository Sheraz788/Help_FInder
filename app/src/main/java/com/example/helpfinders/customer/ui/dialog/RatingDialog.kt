package com.example.helpfinders.customer.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.helpfinders.model.Notification
import com.example.helpfinders.model.Review
import com.example.helpfinders.model.User
import com.example.letsride.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.rating_dialog_layout.*

class RatingDialog(var context : Activity, var user : User, var notification: Notification) : Dialog(context){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        setContentView(R.layout.rating_dialog_layout)

        var fromId = FirebaseAuth.getInstance().uid
        var toId = user.uid

        var toFirebaseReference = FirebaseDatabase.getInstance().getReference("users-reviews/$toId/$fromId")


        btn_submit_rate.setOnClickListener {
            var review = Review(
                rating.rating.toDouble(),
                txt_comments.text.toString()
            )

            toFirebaseReference.setValue(review)
                .addOnSuccessListener {

                    var notificationFromId = notification.fromId
                    var notificationToId  = notification.uid

                    val updateCustomerNotificationRef = FirebaseDatabase.getInstance().getReference("notifications/$notificationToId/$notificationFromId") //customer
                    val updatedCustomerNotification = Notification(
                        notification.uid,
                        notification.fromId,
                        notification.type,
                        notification.viewstatus,
                        notification.timestamp,
                        notification.providerstatus,
                        "Ended",
                        notification.starttime,
                        notification.endtime
                    )
                    updateCustomerNotificationRef.setValue(updatedCustomerNotification)

                    Toast.makeText(context, "Review Added Thanks!!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to Add Review!!", Toast.LENGTH_SHORT).show()
                }


            dismiss()

        }

    }
}