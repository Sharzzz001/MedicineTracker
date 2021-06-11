package com.example.medicinetracker.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicinetracker.DataModels.alarmsModel
import com.example.medicinetracker.PatientMedicines.AddMedActivity
import com.example.medicinetracker.PatientMedicines.editalarm
import com.example.medicinetracker.R
import com.example.medicinetracker.RecyclerViewAdapter.alarmListAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_add_med.*

class AddMed : Fragment(R.layout.fragment_add_med), (alarmsModel)->Unit {
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var alarmlist: List<alarmsModel> = ArrayList()
    private var alarmListAdapter: alarmListAdapter = alarmListAdapter(alarmlist, this)
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentFragment = fragmentManager!!.findFragmentByTag("AddMed")
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        currentFragment?.let { fragmentTransaction.detach(it) }
        currentFragment?.let { fragmentTransaction.attach(it) }
        fragmentTransaction.commit()
        medadd.setOnClickListener {
            val intent = Intent(getActivity(), AddMedActivity::class.java)
            getActivity()?.startActivity(intent)
        }
        loadalarmData()

        alarm_list.layoutManager = LinearLayoutManager(activity)
        alarm_list.adapter = alarmListAdapter
    }
    fun getalarmList(): Task<QuerySnapshot> {
        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        var uid= currentUser!!.uid
        return firebaseFirestore.collection("alarms").whereEqualTo("puid", uid).get()
    }

    private fun loadalarmData(){
        getalarmList().addOnCompleteListener{
            if (it.isSuccessful){
                alarmlist = it.result!!.toObjects(alarmsModel::class.java)
                alarmListAdapter.alarmListItem = alarmlist
                alarmListAdapter.notifyDataSetChanged()
            }
        }
    }

    //RecyclerView OnClick
    override fun invoke(alarmsModel: alarmsModel) {
        //Toast.makeText(this,"clicked on item: ${alarmsModel.name}", Toast.LENGTH_LONG).show()
        var MedName=alarmsModel.MedName
        //var alarmtext=alarmsModel.alarmtext
        val intent = Intent(getActivity(), editalarm::class.java)
        intent.putExtra("medname", MedName)
        getActivity()?.startActivity(intent)
    }
}