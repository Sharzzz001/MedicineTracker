package com.example.medicinetracker.AlarmServices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlertReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val profileName=intent.getStringExtra("Docname")
        val notificationHelper = NotificationHelper(context)
        val nb: NotificationCompat.Builder? = notificationHelper.getChannelNotification()
        var rand=(0..10000000000).random().toInt()
        if (nb != null) {
            notificationHelper.getManager()?.notify(rand, nb.build())
        }
    }
}