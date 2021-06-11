package com.example.medicinetracker.PatientMedicines

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.medicinetracker.AddAlarm.AddAlarmActivity
import com.example.medicinetracker.AlarmServices.rec
import com.example.medicinetracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_med.*
import java.util.*

class AddMedActivity : AppCompatActivity() {
    val db= FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    /*
    val intent = Intent("my.action.string")
        intent.putExtra("extra", phoneNo)
        sendBroadcast(intent)
     */
    var medicinename:String=""
    var potency:String=""
    var dose:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_med)
        //Spinner Code
        var unit: String
        val Units = resources.getStringArray(R.array.TYPE)
        if (typeSpinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, Units
            )
            typeSpinner.adapter = adapter
        }

        //Edit text
        Potency.doOnTextChanged { text, start, count, after ->
            potency=Potency.text.toString()
            /*var intent=Intent(this@AddMedActivity,AddAlarmActivity::class.java)
            intent.putExtra("medcine",medicinename)
            startActivity(intent)*/
        }
        MedName.doOnTextChanged { text, start, count, after ->
            medicinename=MedName.text.toString()
            /*var intent=Intent(this@AddMedActivity,AddAlarmActivity::class.java)
            intent.putExtra("medcine",medicinename)
            startActivity(intent)*/
        }
        //Edit text

        //Spinner
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                unit = Units[position]
                typetext.setText(unit)
            }
        }
        //End of Spinner Code

        //Dose NumberPicker
        if(numberPicker != null) {
            numberPicker.minValue = 0
            numberPicker.maxValue = 15
            numberPicker.wrapSelectorWheel = true
            numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                //val text = "Changed from $oldVal to $newVal"
                dose= newVal
            }
        }
        //End of DosePicker

        //Firestore Database
        btndone.setOnClickListener {
            if (MedName.text.toString().isEmpty()){
                return@setOnClickListener
            }
            else {
                adddata(medicinename, dose, typetext.text as String)
            }
        }

    }

    private fun adddata(medname: String, newVal: Number, typetext1: String){
        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        var uid= currentUser!!.uid
        var docname=uid+medname
        var docdata = hashMapOf(
            "MedName" to medname,
            "dose" to newVal,
            "type" to typetext1,
            "potency" to potency
        )
        val intent = Intent(this@AddMedActivity, AddAlarmActivity::class.java)
        intent.putExtra("medicine", docname)
        intent.putExtra("name0", medname)
        startActivity(intent)
        db.collection("alarms").document(docname).set(docdata).addOnSuccessListener {
            Toast.makeText(this, "Saved alarm", Toast.LENGTH_SHORT).show()
            Log.d("MedicineStore", "stored")
        }
            .addOnFailureListener{
                Toast.makeText(this,
                    "Couldnt save alarm, Contat support: ${it.message}",
                    Toast.LENGTH_SHORT).show()
                Log.d("MedicineStore", "Couldn't be stored: ${it.message}")
            }
    }
}