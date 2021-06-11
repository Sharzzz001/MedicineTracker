package com.example.medicinetracker.AlarmServices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import io.karn.notify.Notify


class rec : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Notify
            .with(context)
            .content { // this: Payload.Content.Default
                title = "Snoozed Alarm"
                text = "its been 10 mins since you snoozed, time for your medicine"
            }
            .show()
    }
}