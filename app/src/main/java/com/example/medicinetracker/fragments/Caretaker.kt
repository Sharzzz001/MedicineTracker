package com.example.medicinetracker.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinetracker.AlarmServices.CaretakerReceiver
import com.example.medicinetracker.PatientDetail
import com.example.medicinetracker.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_caretaker.*
import java.util.*
import kotlin.collections.ArrayList


data class Patientt(
    val pname: String = "",
    val pemail: String = "",
    val puid: String = "",
)

class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
class Caretaker : Fragment(R.layout.fragment_caretaker) {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    private var patientList0: List<String> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler().postDelayed({
            displaypatient()
            caretaker_pbar?.visibility = View.INVISIBLE
            abc?.visibility = View.VISIBLE
        }, 1000)
        sync.setOnClickListener {
            return@setOnClickListener
        }
    }








    /*
    private fun getAlarms(){
        auth = Firebase.auth
        db.collection("alarms")
            .document(auth.currentUser?.uid.toString()).get()
            .addOnCompleteListener { task ->
                var document = task.result
                var timearr = document!!["puid"] as

            }
        val query = db.collection("patient").whereEqualTo("cuid",auth.currentUser?.uid.toString()).orderBy("pname")
        db.collection("alarms").whereEqualTo("cuid",auth.currentUser?.uid.toString()
        )
        FirebaseFirestore.getInstance().collection("alarms")
            .document(docname).get()
            .addOnCompleteListener { task ->
                var document = task.result
                var timearr = document!!["alarmtext"] as List<String>
                var name= document!!["MedName"] as String
                textView8.setText(name)
                //text.setText(timearr[0])
                addarr(timearr)
                arrayAdapter.notifyDataSetChanged()
                //Log.d("myTag", group_string);
            }
    }
     */

    private fun displaypatient() {
        auth = Firebase.auth
        val query = db.collection("patient").whereEqualTo("cuid", auth.currentUser?.uid.toString())
        val options = FirestoreRecyclerOptions.Builder<Patientt>().setQuery(query,
            Patientt::class.java)
            .setLifecycleOwner(this).build()
        val adapter = object: FirestoreRecyclerAdapter<Patientt, PatientViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
                val view = LayoutInflater.from(context?.applicationContext).inflate(R.layout.singlerow,
                    parent,
                    false)
                return PatientViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: PatientViewHolder,
                position: Int,
                model: Patientt,
            ) {
                val ptmail: TextView = holder.itemView.findViewById(R.id.text2user)
                val ptname: TextView = holder.itemView.findViewById(R.id.text1user)
                val ptuid: TextView = holder.itemView.findViewById(R.id.text3user)

                //binding
                ptmail.text = model.pemail
                ptname.text = model.pname
                ptuid.text = model.puid

                ptname.setOnClickListener{
                    val i = Intent(ptname.context, PatientDetail::class.java)
                    i.putExtra("ptname", model.pname)
                    i.putExtra("ptemail", model.pemail)
                    i.putExtra("ptuid", model.puid)

                    //need to setflags to jump recyclerview to new tasks
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(i)
                }

            }

        }
        patients_recyclerview?.adapter = adapter
        adapter.notifyDataSetChanged()
        patients_recyclerview?.layoutManager = LinearLayoutManager(this.context)
        patients_recyclerview?.overScrollMode=View.OVER_SCROLL_NEVER

        Handler().postDelayed({
            checkpatient(adapter)

        },1000)
    }

    private fun checkpatient(adapter: FirestoreRecyclerAdapter<Patientt, PatientViewHolder>) {
        if (adapter.itemCount == 0){
            nopt?.visibility=View.VISIBLE
        }
        else{
            nopt?.visibility = View.INVISIBLE
            sync?.visibility = View.INVISIBLE
        }

    }
}