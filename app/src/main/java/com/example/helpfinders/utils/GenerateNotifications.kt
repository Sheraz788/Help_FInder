package com.example.helpfinders.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.letsride.R
import com.example.helpfinders.provider.ui.chat.ChatLogFragment

class GenerateNotifications {

    companion object{

        lateinit var pendingIntent : PendingIntent
        fun generateNotification(activity : Activity, userName : String, userMessage : String, type : String){


//            if(type == "chat"){
//                // Create an explicit intent for an Activity/Fragment in your app
//                val intent = Intent(activity, ChatLogFragment::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//
//                pendingIntent = PendingIntent.getActivities(activity, 0, arrayOf(intent), 0)
//            }
            val builder = NotificationCompat.Builder(activity, "notification")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(userName)
                .setContentText(userMessage)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                    .bigText(userMessage))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)


            with(NotificationManagerCompat.from(activity)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
            }
        }

        private fun createNotificationChannel(activity : Activity) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = activity.getString(R.string.channel_name)
                val descriptionText = activity.getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("notification", name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

    }

}