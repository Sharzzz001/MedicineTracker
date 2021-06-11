package com.example.medicinetracker.caretakeraddalarms

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.medicinetracker.AddAlarm.AddAlarmActivity
import com.example.medicinetracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_ct_add_med.*


class CtAddMed : AppCompatActivity() {
    var medicinename:String=""
    var dose:Int=0
    val db= FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ct_add_med)

        ptname.setText(ptname.text.toString() + intent.getStringExtra("ptname"))

        var unit: String
        val Units = resources.getStringArray(R.array.TYPE)
        if (typeSpinner12 != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, Units
            )
            typeSpinner12.adapter = adapter
        }

        MedName12.doOnTextChanged { text, start, count, after ->
            medicinename=MedName12.text.toString()
            /*var intent=Intent(this@AddMedActivity,AddAlarmActivity::class.java)
            intent.putExtra("medcine",medicinename)
            startActivity(intent)*/
        }

        typeSpinner12.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                unit = Units[position]
                typetext12.setText(unit)
            }
        }

        if(numberPicker12 != null) {
            numberPicker12.minValue = 0
            numberPicker12.maxValue = 15
            numberPicker12.wrapSelectorWheel = true
            numberPicker12.setOnValueChangedListener { picker, oldVal, newVal ->
                //val text = "Changed from $oldVal to $newVal"
                dose= newVal
            }
        }

        btndone12.setOnClickListener {
            if (MedName12.text.toString().isEmpty()){
                return@setOnClickListener
            }
            else {
                adddata(medicinename, dose, typetext12.text as String)
            }
        }

    }

    private fun adddata(medname:String,newVal:Number,typetext1:String) {
        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        var uid= intent.getStringExtra("ptuid")
        var docname=uid+medname
        var docdata = hashMapOf(
            "MedName" to medname,
            "dose" to newVal,
            "type" to typetext1,
        )
        val pname = intent.getStringExtra("ptname")
        val puid = intent.getStringExtra("ptuid")
        val intent = Intent(this, CtAddAlarm::class.java)
        intent.putExtra("medicine",docname)
        intent.putExtra("name0",medname)
        intent.putExtra("patname",pname)
        intent.putExtra("patuid",puid)
        startActivity(intent)
        db.collection("alarms").document(docname).set(docdata).addOnSuccessListener {
            Toast.makeText(this,"Saved patients alarm", Toast.LENGTH_SHORT).show()
            Log.d("MedicineStore","stored")
        }
            .addOnFailureListener{
                Toast.makeText(this, "Couldnt save alarm, Contact support: ${it.message}", Toast.LENGTH_SHORT).show()
                Log.d("MedicineStore","Couldn't be stored: ${it.message}")
            }

    }
}