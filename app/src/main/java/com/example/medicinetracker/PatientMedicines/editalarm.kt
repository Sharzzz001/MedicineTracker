package com.example.medicinetracker.PatientMedicines

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.medicinetracker.AlarmServices.AlertReceiver
import com.example.medicinetracker.AlarmServices.TimePickerFragment
import com.example.medicinetracker.AlarmServices.rec
import com.example.medicinetracker.MainActivity
import com.example.medicinetracker.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_alarm.*
import kotlinx.android.synthetic.main.activity_editalarm.*
import org.jetbrains.anko.toast
import java.text.DateFormat
import java.util.*

class editalarm : AppCompatActivity(), TimePickerDialog.OnTimeSetListener{
    val db = FirebaseFirestore.getInstance()
    lateinit var arrayAdapter: ArrayAdapter<String>
    var timearr0: ArrayList<String> = ArrayList()
    var calendararr0: ArrayList<Calendar> = ArrayList()
    var timetextarr0: ArrayList<String> = ArrayList()
    var flag=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editalarm)

        var medname=intent.getStringExtra("medname")
        val uid = Firebase.auth.currentUser?.uid.toString()
        var docname=uid+medname

        FirebaseFirestore.getInstance().collection("alarms")
            .document(docname).get()
            .addOnCompleteListener { task ->
                var document = task.result
                var timearr = document!!["alarmtext"] as List<String>
                var name= document!!["MedName"] as String
                var type1 = document!!["type"] as String
                var poten = document!!["potency"] as String
                pot.setText(poten)
                textView8.setText(name)
                name2.setText(name.toUpperCase())
                type.setText(type1)
                //text.setText(timearr[0])
                addarr(timearr)
                arrayAdapter.notifyDataSetChanged()
                //Log.d("myTag", group_string);
            }
        deletenew.setOnClickListener {
            showAlertDialog(docname)
        }
        addnew.setOnClickListener {
            if(flag==0){
                showAlertDialog1(docname)
            }
            else{
                val timePicker: DialogFragment = TimePickerFragment()
                timePicker.show(supportFragmentManager, "time picker")
            }
        }
        save.setOnClickListener {
            addarr1(docname)
        }
        snoozeButton.setOnClickListener {
            //snoozes alarm by 10 mins
            toast("Snoozed alarm")
            val date = Date()
            getCurrentTime(date)
        }
    }

    private fun showAlertDialog1(docname: String) {
        flag=100
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@editalarm)
        alertDialog.setTitle("Warning")
        alertDialog.setMessage("Setting new Alarms will reset previously set alarms")
        alertDialog.setPositiveButton(
            "yes"
        ) { _, _ ->
            FirebaseFirestore.getInstance().collection("alarms")
                .document(docname).get()
                .addOnCompleteListener { task ->
                    var document = task.result
                    var timearrmill = document!!["alarms"] as List<String>
                    for (i in 0 until timearrmill.size) {
                        setdata(docname, timearrmill[i])
                        cancelAllAlarm(timearrmill[i].toInt())
                    }
                }
            val timePicker: DialogFragment = TimePickerFragment()
            timePicker.show(supportFragmentManager, "time picker")
        }
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun addarr(timearr: List<String>) {
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, timearr)
        ListView2!!.adapter=arrayAdapter
    }

    private fun showAlertDialog(docname: String) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@editalarm)
        alertDialog.setTitle("Warning")
        alertDialog.setMessage("Are you sure you want to delete your alarms")
        alertDialog.setPositiveButton(
            "yes"
        ) { _, _ ->
            FirebaseFirestore.getInstance().collection("alarms")
                .document(docname).get()
                .addOnCompleteListener { task ->
                    var document = task.result
                    var timearrmill = document!!["alarms"] as List<String>
                    for (i in 0 until timearrmill.size){
                        setdata(docname, timearrmill[i])
                        cancelAllAlarm(timearrmill[i].toInt())
                    }

                    deleteData(docname)
                    startActivity(Intent(this, MainActivity::class.java))
                }
        }
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    private fun setdata(docname: String, s: String) {
        val hmap = hashMapOf(
            "alarmid" to s,
            "puid" to Firebase.auth.currentUser?.uid.toString()
        )
        db.collection("deletedalarms").document(docname).set(hmap)
            .addOnCompleteListener{
                Log.d("del", "Deleted succesffuly:$s")
            }
            .addOnFailureListener{
                Log.d("del", "Reason: ${it.message}")
            }

    }

    private fun deleteData(docname: String) {
        val doc=db.collection("alarms").document(docname)
        doc.delete()
    }

    private fun cancelAllAlarm(s: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, s, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c[Calendar.HOUR_OF_DAY] = hourOfDay
        c[Calendar.MINUTE] = minute
        c[Calendar.SECOND] = 0
        updateTimeText(c)
        startAlarm(c)
        calendararr0.add(c)//store all calendar data into an array
    }

    private fun startAlarm(c: Calendar) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        var timo = c.timeInMillis.toInt()
        timearr0.add(timo.toString())
        arrayAdapter.notifyDataSetChanged()
        val pendingIntent = PendingIntent.getBroadcast(this, timo, intent, 0)
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
            c.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent)
    }

    private fun updateTimeText(c: Calendar) {
        var timeText: String? = ""
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.time)
        if (timeText != null) {
            timetextarr0.add(timeText)
        }
    }

    private fun addarr1(docname: String){
        var docdata = hashMapOf(
            "alarms" to timearr0,
            "alarmtext" to timetextarr0
        )
        db.collection("alarms").document(docname).update(docdata as Map<String, Any>).addOnSuccessListener {
            toast("saved")
        }
        addarr(timetextarr0)
        arrayAdapter.notifyDataSetChanged()
    }

    fun getCurrentTime(date: Date?) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[Calendar.MINUTE]+=10
        startAlarm1(calendar)
    }

    private fun startAlarm1(c: Calendar) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, rec::class.java)
        var timo = c.timeInMillis.toInt()
        val pendingIntent = PendingIntent.getBroadcast(this, timo, intent, 0)
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }
}