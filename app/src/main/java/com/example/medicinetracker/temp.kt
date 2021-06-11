package com.example.medicinetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import io.karn.notify.Notify

class DeleterReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val profileName=intent.getStringExtra("course")
        this
        Notify
            .with(context)
            .content { // this: Payload.Content.Default
                title = "title"
                text = profileName
            }
            .show()
    }
}