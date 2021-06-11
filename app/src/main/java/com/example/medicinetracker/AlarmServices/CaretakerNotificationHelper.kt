package com.example.medicinetracker.AlarmServices

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.medicinetracker.MainActivity
import com.example.medicinetracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CaretakerNotificationHelper(base: Context?) : ContextWrapper(base) {
    private lateinit var auth: FirebaseAuth
    private var mManager: NotificationManager? = null
    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel =
            NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
        getManager()!!.createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager? {
        if (mManager == null) {
            mManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
        return mManager
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getChannelNotification(): NotificationCompat.Builder? {
        var MedName=""
        auth= Firebase.auth
        var name=auth.currentUser?.uid.toString()
        var person=auth.currentUser?.displayName.toString()
        var resultIntent = Intent(this, MainActivity::class.java)
        var current = LocalDateTime.now()
        //End

        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formatted = current.format(formatter)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle("Hey! $person")
            .setContentText("It's $formatted, it's time for one of your patients to take their medicine")
            .setSmallIcon(R.drawable.pill)
            .setContentIntent(resultPendingIntent)
    }

    companion object {
        const val channelID = "channelID"
        const val channelName = "Channel Name"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }
}