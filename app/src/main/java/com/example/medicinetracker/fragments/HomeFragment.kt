package com.example.medicinetracker.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_med.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home), (alarmsModel)->Unit  {
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var alarmlist: List<alarmsModel> = ArrayList()
    private var alarmListAdapter: alarmListAdapter = alarmListAdapter(alarmlist, this)
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //hometext.visibility = View.GONE
        invbtn.setOnClickListener{
            //code
            val user = Firebase.auth.currentUser!!
            val uid = user.uid
            val email = user.email
            val name = user.displayName
            val invitationLink = "https://medicinetracker.page.link/?invitedby=$uid&uemail=$email&uname=$name"
            //Log.d("Main","Email: ${invitationLink}")
            Firebase.dynamicLinks.shortLinkAsync {
                link = Uri.parse(invitationLink)
                domainUriPrefix = "https://medicinetracker.page.link"
                androidParameters("com.example.medicinetracker") {
                }
                iosParameters("com.example.ios") {
                    appStoreId = "123456789"
                    minimumVersion = "1.0.1"
                }
            }.addOnSuccessListener { shortDynamicLink ->
                var mInvitationUrl = shortDynamicLink.shortLink
                val referrerName = Firebase.auth.currentUser?.email.toString()
                //val subject = String.format("%s wants you to join on Medtrack", referrerName)
                val invitationLink = mInvitationUrl.toString()
                val msg = "Let's medtrack together! Use my referrer link: $invitationLink.\n Sent by $referrerName"
                Log.d("Main", msg)
                //val msgHtml = String.format("<p>Let's medtrack together! Use my " + "<a href=\"%s\">referrer link</a>!</p>", invitationLink)

//                val intent = Intent(Intent.ACTION_SENDTO).apply {
//                    data = Uri.parse("mailto:") // only email apps should handle this
//                    putExtra(Intent.EXTRA_SUBJECT, subject)
//                    putExtra(Intent.EXTRA_TEXT, msg)
//                    putExtra(Intent.EXTRA_HTML_TEXT, msgHtml)


                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                sendIntent.type = "text/plain"
                startActivity(sendIntent)


//                intent.resolveActivity(packageManager)?.let {
//                    startActivity(intent)
//                }


            }.addOnFailureListener {
                Log.d("Main", "Error : ${it.message}")
            }


        }
        addbutton.setOnClickListener{
            val intent = Intent(getActivity(), AddMedActivity::class.java)
            getActivity()?.startActivity(intent)
        }

        loadalarmData()

        home_list?.layoutManager = LinearLayoutManager(activity)
        home_list?.adapter = alarmListAdapter


    }

    fun getalarmList(): Task<QuerySnapshot> {
        auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        var uid = currentUser!!.uid
        return firebaseFirestore.collection("alarms").whereEqualTo("puid", uid).get()
    }

    private fun loadalarmData() {
        getalarmList().addOnCompleteListener {
            if (it.isSuccessful) {
                alarmlist = it.result!!.toObjects(alarmsModel::class.java)
                alarmListAdapter.alarmListItem = alarmlist
                alarmListAdapter.notifyDataSetChanged()
                if (alarmlist.isEmpty()){
                    textView2?.visibility = View.VISIBLE
                    textView3?.visibility = View.VISIBLE
                    addbutton?.visibility = View.VISIBLE
                    home_list?.visibility = View.INVISIBLE
                    //cardview12?.visibility = View.INVISIBLE
                }else{
                    cardview12?.visibility = View.VISIBLE
                    textView2?.visibility = View.GONE
                    textView3?.visibility = View.GONE
                    addbutton?.visibility = View.GONE
                }
            }
        }
    }
    override fun invoke(alarmsModel: alarmsModel) {
        //Toast.makeText(this,"clicked on item: ${alarmsModel.name}", Toast.LENGTH_LONG).show()
        var MedName=alarmsModel.MedName
        //var alarmtext=alarmsModel.alarmtext
        val intent = Intent(getActivity(), editalarm::class.java)
        intent.putExtra("medname", MedName)
        getActivity()?.startActivity(intent)
    }
}