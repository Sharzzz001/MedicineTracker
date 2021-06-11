package com.example.medicinetracker.caretakeraddalarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.medicinetracker.AlarmServices.CaretakerReceiver
import com.example.medicinetracker.AlarmServices.TimePickerFragment
import com.example.medicinetracker.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_ct_add_alarm.*
import org.jetbrains.anko.toast
import java.text.DateFormat
import java.util.*

class CtAddAlarm : AppCompatActivity() , TimePickerDialog.OnTimeSetListener{
    val db= FirebaseFirestore.getInstance()
    private var mTextView: TextView? = null
    var timearr: ArrayList<String> = ArrayList()
    var calendararr: ArrayList<Calendar> = ArrayList()
    var timetextarr: ArrayList<String> = ArrayList()
    lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ct_add_alarm)

        var medicinename=intent.getStringExtra("name0")
        testy12.text = medicinename
        name112.setText(intent.getStringExtra("name0").toString().toUpperCase())

        mTextView = findViewById(R.id.textView12)
        var listview1=findViewById<ListView>(R.id.listview112)
        var btndone=findViewById<Button>(R.id.done12)
        val buttonCancelAlarm = findViewById<Button>(R.id.button_cancel12)
        val buttonTimePicker = findViewById<ImageButton>(R.id.button_timepicker12)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, timetextarr)
        listview1.adapter=arrayAdapter

        listview1.setOnItemClickListener { adapterView, view, position: Int, id: Long ->
            cancelAlarm(timearr[position].toInt())
            arrayAdapter.remove(arrayAdapter.getItem(position))
            arrayAdapter.notifyDataSetChanged()
            timearr.removeAt(position)
            toast("Deleted alarm")
        }

        buttonTimePicker.setOnClickListener {
            arrayAdapter.notifyDataSetChanged()
            val timePicker: DialogFragment = TimePickerFragment()
            timePicker.show(supportFragmentManager, "time picker")
        }

        buttonCancelAlarm.setOnClickListener {
            var size=timearr.size
            for (i in 0 until size ){
                cancelAllAlarm(timearr[i].toInt())
            }
            arrayAdapter.clear()
            arrayAdapter.notifyDataSetChanged()
            timearr.clear()
            toast("Deleted all alarms")
        }

        btndone.setOnClickListener {
            //this saves alarms to the database
            var medicinename=intent.getStringExtra("medicine").toString()
            addarr(medicinename)
        }

    }

    private fun addarr(medi:String) {
        val uid = intent.getStringExtra("patuid")
        val ptname = intent.getStringExtra("patname")
        var docdata = hashMapOf(
            "alarms" to timearr,
            "alarmtext" to timetextarr,
            "puid" to uid,
            "pname" to ptname,
            "interval" to "Alarms Not Repetitive"
        )
        db.collection("alarms").document(medi).update(docdata as Map<String, Any>).addOnSuccessListener {
            toast("saved")
        }
    }

    private fun cancelAllAlarm(s: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, CaretakerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, s, intent, 0)
        alarmManager.cancel(pendingIntent)
        mTextView!!.text = "All Alarms cancelled"
    }

    private fun cancelAlarm(s: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, CaretakerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, s, intent, 0)
        alarmManager.cancel(pendingIntent)
        mTextView!!.text = "Alarm cancelled"
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c[Calendar.HOUR_OF_DAY] = hourOfDay
        c[Calendar.MINUTE] = minute
        c[Calendar.SECOND] = 0
        updateTimeText(c)
        startAlarm(c)
        calendararr.add(c)//store all calendar data into an array

        //to send alarm to caretaker
        var docdata = hashMapOf(
            "hour" to hourOfDay.toString(),
            "minute" to minute.toString(),
            "second" to 0
        )

        db.collection("alarms").document(intent.getStringExtra("medicine").toString()).update(docdata as Map<String, Any>)
            .addOnSuccessListener { Log.d("main12","saved hour") }
    }

    private fun startAlarm(c: Calendar) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, CaretakerReceiver::class.java)
        var timo = c.timeInMillis.toInt()
        timearr.add(timo.toString())
        arrayAdapter.notifyDataSetChanged()
        val pendingIntent = PendingIntent.getBroadcast(this, timo, intent, 0)
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    private fun updateTimeText(c: Calendar) {
        var timeText: String? = ""
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.time)
        if (timeText != null) {
            timetextarr.add(timeText)
        }
        mTextView!!.text = timeText
    }
}