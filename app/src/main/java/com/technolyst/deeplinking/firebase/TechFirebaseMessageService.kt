package com.technolyst.deeplinking.firebase

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.technolyst.deeplinking.MainActivity
import com.technolyst.deeplinking.R
import com.technolyst.deeplinking.dataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TechFirebaseMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//When App in background notification is handle by system
        // and it used notification payload and used title and body

        // and app in foreground i am using data payload

        // now send only data payload on that case onMessageRecievd also called in background.


        //Log incoming message
        Log.v("CloudMessage", "From ${message.from}")

        //Log Data Payload
        if (message.data.isNotEmpty()) {
            Log.v("CloudMessage", "Message Data ${message.data}")
        }

        //Check if message contains a notification payload

        message.data.let {
            Log.v("CloudMessage", "Message Data Body ${it["body"]}")
            Log.v("CloudMessage", "Message Data Title  ${it["title"]}")
            //when app in forground that notification is not shown on status bar
            //lets write a code to display notification in status bar when app in forground.
            showNotificationOnStatusBar(it)
        }

        if (message.notification != null) {

            Log.v("CloudMessage", "Notification ${message.notification}")
            Log.v("CloudMessage", "Notification Title ${message.notification!!.title}")
            Log.v("CloudMessage", "Notification Body ${message.notification!!.body}")

        }

    }

    private fun showNotificationOnStatusBar(data: Map<String, String>) {

        //Create Intent it will be launched when user tap on notification from status bar.
        val intent = Intent(this, MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        intent.putExtra("title", data["title"])
        intent.putExtra("body", data["body"])
        // in JSON payload we will send page name also so we can create page Uri deeplink
        //Lets create deeplink Intent

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW, ("deeplink://" + data["page"]).toUri(),
            this, MainActivity::class.java
        )

        // it should be unqiue when push comes.
        //Update pendingIntent code and used TaskStackBuilder to create pending intent for our jetpack compose navigation
        var requestCode = System.currentTimeMillis().toInt()
        var pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(deepLinkIntent).getPendingIntent(
                    requestCode,
                    FLAG_MUTABLE
                )
            }
        } else {
            TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(deepLinkIntent).getPendingIntent(
                    requestCode,
                    FLAG_CANCEL_CURRENT
                )
            }
        }

        val builder = NotificationCompat.Builder(this,"Global").setAutoCancel(true)
            .setContentTitle(data["title"])
            .setContentText(data["body"])
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText((data["body"])))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_notification)


        with(NotificationManagerCompat.from(this)){
            notify(requestCode,builder.build())
        }

        //Let's run it.


    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM",token)
        GlobalScope.launch {
            saveGCMToken(token)
        }
    }

    //Save GCM Token DataStore Preference
    // you can used to send it on your Server.
    private suspend fun saveGCMToken(token: String) {
        val gckTokenKey = stringPreferencesKey("gcm_token")
        baseContext.dataStore.edit { pref ->
            pref[gckTokenKey] = token
        }
    }
}