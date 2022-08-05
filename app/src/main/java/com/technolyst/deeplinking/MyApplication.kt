package com.technolyst.deeplinking

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

//Create Singleton instance of DataStore Preference
val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "LocalStore")
class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        //Let's call the function.
        createNotificationChannel()
        subscribeGlobalTopic()
    }

    private fun subscribeGlobalTopic() {
        Firebase.messaging.subscribeToTopic("Global").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase","All Global Topic Subscribe")
            } else {
                Log.d("Firebase","All Global Topic Subscribe Failed.")

            }
        }
    }

    //Create Notification Channel.
    private fun createNotificationChannel(){
        val name = "Deep Linking"
        val description ="DeepLinking "
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        //Now Create Notification Channel.
        // it take three parameters. notification id,name, and importance.
        val channel = NotificationChannel("Global",name,importance)
        channel.description = description;

        // Get Notification Manager.
        val notificationManager : NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Lets Create Notification channel.
        notificationManager.createNotificationChannel(channel)

    }

}