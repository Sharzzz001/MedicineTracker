package com.example.medicinetracker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.medicinetracker.AlarmServices.CaretakerReceiver
import com.example.medicinetracker.fragments.AddMed
import com.example.medicinetracker.fragments.Caretaker
import com.example.medicinetracker.fragments.HomeFragment
import com.example.medicinetracker.fragments.Userprofile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

private val homeFragment = HomeFragment()
private val addmed = AddMed()
private val profile = Userprofile()
private val caretaker = Caretaker()

class MainActivity : AppCompatActivity() {
    private var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(homeFragment)
        getAlarms()

       // Toast.makeText(this,"User: ${Firebase.auth.currentUser?.displayName.toString()}",Toast.LENGTH_SHORT).show()
        bottomnav.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.ic_home -> replaceFragment(homeFragment)
                R.id.ic_add -> replaceFragment(addmed)
                R.id.ic_profile -> replaceFragment(profile)
                R.id.ic_caretaker -> replaceFragment(caretaker)
            }
            true
        }
    }

    private fun getAlarms(){
        var pat: ArrayList<String> = ArrayList()
        pat.clear()
        db.collection("caretakers").whereEqualTo("cuid", Firebase.auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //Log.d("Main", "${document.data}")
                    val patientArray=document["puid"] as String
                    pat.add(patientArray)
                    //Log.d("Main", "Time: ${pat}")

                    //val alarmuid = document["alarms"] as List<*>?
                    //Log.d("Main", "Alarms: ${alarmuid}")
                }
                for (i in 0 until pat.size){
                    getpatientdetails(pat[i])
                }

            }
            .addOnFailureListener{
                Log.d("Main", "Error")
            }
    }

    private fun getpatientdetails(s: String) {
        db.collection("alarms").whereEqualTo("puid", s).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    Log.d("Main", "${document.data}")
                    val calendararr=document["alarms"] as ArrayList<String>

                    Log.d("mainabcd", "A: $calendararr")
                    var hour = document.getString("hour")
                    var minute = document.getString("minute")


                    for(x in 0 until calendararr.size){

                        if (hour != null) {
                            startAlarm(hour,minute)
                        }

                    }
                    //val alarmuid = document["alarms"] as List<*>?
                    //Log.d("Main", "Alarms: ${alarmuid}")
                }
            }
            .addOnFailureListener{
                Log.d("Main", "Error")
            }
    }

    private fun startAlarm(hour: String?, minute: String?) {

        val c = Calendar.getInstance()
        if (hour != null)  {
            c[Calendar.HOUR_OF_DAY] = hour.toInt()
            if (minute != null) {
                c[Calendar.MINUTE] = minute.toInt()
                c[Calendar.SECOND] = 0
            }
        }
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, CaretakerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, c.timeInMillis.toInt(), intent, 0)
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    //Signout by double back

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            FirebaseAuth.getInstance().signOut()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to signout", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)

    }

}