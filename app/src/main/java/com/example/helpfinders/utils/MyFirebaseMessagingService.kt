package com.example.helpfinders.utils

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.letsride.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){

    private var broadCaster : LocalBroadcastManager? = null

    override fun onCreate() {
        super.onCreate()
        broadCaster = LocalBroadcastManager.getInstance(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        handleRemoteMessage(remoteMessage)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }

    fun handleRemoteMessage(remoteMessage: RemoteMessage){
        val handler = Handler(Looper.getMainLooper())

        handler.post(Runnable {
            Toast.makeText(baseContext, getString(R.string.handle_notification_now),
                Toast.LENGTH_LONG).show()

            remoteMessage.notification.let {
                val intent = Intent("MyData")
                intent.putExtra("message", remoteMessage.data["text"])
                broadCaster?.sendBroadcast(intent)
            }
        })
    }

}