package com.example.medicinetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinetracker.DataModels.PatinetalarmModel
import com.example.medicinetracker.DataModels.alarmsModel
import com.example.medicinetracker.RecyclerViewAdapter.alarmListAdapter
import com.example.medicinetracker.RecyclerViewAdapter.patientListAdapter
import com.example.medicinetracker.caretakeraddalarms.CtAddMed
import com.example.medicinetracker.fragments.Caretaker
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_patient_detail.*
import org.jetbrains.anko.toast


class PatientDetail : AppCompatActivity() {
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var alarmlist: List<PatinetalarmModel> = ArrayList()
    private var patientListAdapter: patientListAdapter = patientListAdapter(alarmlist)
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)
        auth = Firebase.auth
        ptname.text = "Name: "+intent.getStringExtra("ptname")
        ptemail.text = "Email: "+intent.getStringExtra("ptemail")

        loadptalarms()
        ptalarmsrecview.layoutManager = LinearLayoutManager(this)
        ptalarmsrecview.adapter = patientListAdapter


    }


    private fun loadptalarms() {
        getptalarms().addOnCompleteListener{
            if (it.isSuccessful){
                alarmlist = it.result!!.toObjects(PatinetalarmModel::class.java)
                patientListAdapter.ptalarmListItem = alarmlist
                patientListAdapter.notifyDataSetChanged()
                if (patientListAdapter.itemCount == 0){
                    noitem.visibility = View.VISIBLE
                }
                else{
                    noitem.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun getptalarms(): Task<QuerySnapshot> {
        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        var uid= currentUser!!.uid
        return firebaseFirestore.collection("alarms").whereEqualTo("puid", intent.getStringExtra("ptuid").toString()).get()
    }

    fun delPatient(view: View) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Caution")
        alertDialog.setMessage("Do you want to delete this patient?")
        alertDialog.setIcon(R.drawable.warn)
        alertDialog.setPositiveButton("yes") { _, _ ->

            ptpbar?.visibility=View.VISIBLE
            val rootRef = FirebaseFirestore.getInstance()
            val itemsRef = rootRef.collection("patient")
            val query: Query = itemsRef.whereEqualTo("cuid", auth.currentUser?.uid.toString()).whereEqualTo("pemail", intent.getStringExtra("ptemail"))
            query.get().addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    for (document in task.result) {
                        itemsRef.document(document.id).delete()
                        Log.d("Main", "Deleted from patient")
                    }
                } else {
                    Log.d("Main", "Error getting documents: ", task.exception)
                }
            }
                .addOnFailureListener{
                    Log.d("Main","Query wrong")
                }
            val ctref = rootRef.collection("caretakers")
            ctref.whereEqualTo("cuid",Firebase.auth.currentUser?.uid.toString()).whereEqualTo("pemail",intent.getStringExtra("ptemail"))
                .get().addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        for (document in task.result){
                            ctref.document(document.id).delete()
                            Log.d("Main","Deleted from caretakers")
                            ptpbar.visibility = View.INVISIBLE
                            toast("Deleted patient successfully")
                            startActivity(Intent(this,MainActivity::class.java))

                        }
                    }
                    else {
                        Log.d("Main", "Error getting documents: ", task.exception)
                    }

                }
                .addOnFailureListener{
                    Log.d("Main","Errorrr: ${it.message}")
                }


        }
        alertDialog.setNegativeButton(
            "No"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert?.setCanceledOnTouchOutside(true)
        alert?.show()







    }

    fun addAlarm(view: View) {
        val pid = intent.getStringExtra("ptuid")
        val pname = intent.getStringExtra("ptname")
        val i = Intent(this,CtAddMed::class.java)
        i.putExtra("ptuid",pid.toString())
        i.putExtra("ptname",pname.toString())
        startActivity(i)

    }


}