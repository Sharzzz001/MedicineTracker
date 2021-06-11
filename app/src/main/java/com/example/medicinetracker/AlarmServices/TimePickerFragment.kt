package com.example.medicinetracker.AlarmServices

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.*


class TimePickerFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]
        return TimePickerDialog(
            activity,
            activity as TimePickerDialog.OnTimeSetListener?,
            hour,
            minute,
            DateFormat.is24HourFormat(
                activity
            )
        )
    }
}