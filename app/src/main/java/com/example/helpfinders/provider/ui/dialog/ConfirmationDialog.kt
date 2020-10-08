package com.example.helpfinders.provider.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.navigation.findNavController
import com.example.helpfinders.model.Notification
import com.example.helpfinders.model.User
import com.example.helpfinders.provider.ui.home.HomeFragment
import com.example.letsride.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.confirmation_dialog_layout.*
import java.util.*
import kotlin.collections.HashMap

class ConfirmationDialog (var context : Activity, var status : String, var notification: Notification, var user: User) : Dialog(context){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.confirmation_dialog_layout)

        if(status == "Start") {
            tv_message.text = "Do you want to start service?"
        }else if (status == "End"){
            tv_message.text = "Do you want to end service?"
        }

        tv_confirm.setOnClickListener {
            val type = notification.type
            val fromId = notification.fromId
            val toId = notification.uid

            if(status == "Start") {
                // updating notification
                val time = Calendar.getInstance().timeInMillis //start time
                val updateCustomerNotificationRef = FirebaseDatabase.getInstance().getReference("notifications/$toId/$fromId") //customer
                val updatedCustomerNotification = Notification(
                    notification.uid,
                    notification.fromId,
                    notification.type,
                    notification.viewstatus,
                    notification.timestamp,
                    "End",
                    "In Progress",
                    time,
                    notification.endtime,
                    notification.servicename,
                    notification.serviceprice
                )
                updateCustomerNotificationRef.setValue(updatedCustomerNotification)

                val updateProviderNotificationRef = FirebaseDatabase.getInstance().getReference("notifications/$fromId/$toId") //provider
                val updatedProviderNotification = Notification(
                    notification.uid,
                    notification.fromId,
                    notification.type,
                    notification.viewstatus,
                    notification.timestamp,
                    "End",
                    "In Progress",
                    time,
                    notification.endtime,
                    notification.servicename,
                    notification.serviceprice
                )
                updateProviderNotificationRef.setValue(updatedProviderNotification)

                dismiss()
            }else if (status == "End"){
                // updating notification
                val time = Calendar.getInstance().timeInMillis // end time
                val updateCustomerNotificationRef = FirebaseDatabase.getInstance().getReference("notifications/$toId/$fromId") //customer
                val updatedCustomerNotification = Notification(
                    notification.uid,
                    notification.fromId,
                    notification.type,
                    notification.viewstatus,
                    notification.timestamp,
                    "Rate",
                    "Rate",
                    notification.starttime,
                    time,
                    notification.servicename,
                    notification.serviceprice
                )
                updateCustomerNotificationRef.setValue(updatedCustomerNotification)

                val updateProviderNotificationRef = FirebaseDatabase.getInstance().getReference("notifications/$fromId/$toId") //provider

                val updatedProviderNotification = Notification(
                    notification.uid,
                    notification.fromId,
                    notification.type,
                    notification.viewstatus,
                    notification.timestamp,
                    "Rate",
                    "Rate",
                    notification.starttime,
                    time,
                    notification.servicename,
                    notification.serviceprice
                )
                updateProviderNotificationRef.setValue(updatedProviderNotification)
                showPaymentDetailsDialog(context, updatedProviderNotification, user)
               // it.findNavController().popBackStack()
                dismiss()
            }
            // adding notification
             val updateStatusRef = FirebaseDatabase.getInstance().getReference("notifications/$toId/$fromId/viewstatus")
                    updateStatusRef.setValue(false)
        }

        tv_cancel.setOnClickListener {
            dismiss()
        }

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