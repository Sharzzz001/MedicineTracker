package com.example.medicinetracker.fragments


import android.animation.Animator
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicinetracker.*
import com.example.medicinetracker.LoginRegister.login
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_userprofile.*
import kotlinx.android.synthetic.main.fragment_userprofile.profilepic

data class Caretakerr(
    val cemail: String = "",
    val cname: String = ""
)

class CaretakerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class Userprofile : Fragment(R.layout.fragment_userprofile) {
    

    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth=Firebase.auth
        //setting user image
        profilepic.setImageDrawable(resources.getDrawable(R.drawable.man))

        displaypic()




        //Showing data from firebase
        showprofile()
        Handler().postDelayed({
            showcaretaker()

        },1000)



        editprof.setOnClickListener{
            startActivity(Intent(this.context,editprofile::class.java))
        }
        profilesignout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this.activity, login::class.java))
            //Log.d("Main","User ID: ${FirebaseAuth.getInstance().currentUser?.uid}")
        }


        //Invite users to app

        profileinvite.setOnClickListener{
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

    }

    private fun displaypic() {
        val stoeref = FirebaseStorage.getInstance().reference.child("pics/${Firebase.auth.currentUser?.uid.toString()}")
        stoeref.downloadUrl.addOnSuccessListener { task ->
            Picasso.get().load(task).into(profilepic)
        }
            .addOnFailureListener{
                profilepic.setImageDrawable(resources.getDrawable(R.drawable.man))
            }
    }

    private fun showcaretaker() {
        val query= db.collection("caretakers").whereEqualTo("puid",auth.currentUser?.uid.toString())
        val options = FirestoreRecyclerOptions.Builder<Caretakerr>().setQuery(query, Caretakerr::class.java)
            .setLifecycleOwner(this).build()

        val adapter = object: FirestoreRecyclerAdapter<Caretakerr, CaretakerViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaretakerViewHolder {
                val view = LayoutInflater.from(context?.applicationContext).inflate(R.layout.singlerow, parent, false)
                return CaretakerViewHolder(view)


            }

            override fun onBindViewHolder(
                holder: CaretakerViewHolder,
                position: Int,
                model: Caretakerr,
            ) {
                val ctmail: TextView = holder.itemView.findViewById(R.id.text2user)
                val ctname: TextView = holder.itemView.findViewById(R.id.text1user)

                //binding
                ctmail.text = model.cemail
                ctname.text = model.cname

                //itemclick
                ctname.setOnClickListener{
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle("Caution")
                    alertDialog.setMessage("Do you want to Delete this caretaker?")
                    alertDialog.setIcon(R.drawable.warn)
                    alertDialog.setPositiveButton("yes") { _, _ ->
                        auth = Firebase.auth
                        val rootRef = FirebaseFirestore.getInstance()
                        val itemsRef = rootRef.collection("caretakers")
                        val query: Query = itemsRef.whereEqualTo("puid", auth.currentUser?.uid.toString()).whereEqualTo("cemail", ctmail.text.toString())
                        query.get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                for (document in task.result) {
                                    itemsRef.document(document.id).delete()
                                    Log.d("Main", "Deleted from caretaker")
                                }
                            } else {
                                Log.d("Main", "Error getting documents: ", task.exception)
                            }
                        }
                            .addOnFailureListener{
                                Log.d("Main","Query wrong")
                            }

                        //deleting from patients
                        val ptref = rootRef.collection("patient")
                        ptref.whereEqualTo("puid",auth.currentUser?.uid.toString()).whereEqualTo("cemail",ctmail.text.toString())
                            .get().addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    for (document in task.result){
                                        ptref.document(document.id).delete()
                                        Log.d("Main","Deleted from patients")

                                        Toast.makeText(context, "Deleted caretaker Successfully", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(context,MainActivity::class.java))
                                    }
                                }
                                else {
                                    Log.d("Main", "Error getting documents: ", task.exception)
                                }

                            }

                            .addOnFailureListener{
                                Log.d("Main", "Patient could not be deleted : ${it.message}")
                            }


                    }
                    alertDialog.setNegativeButton(
                            "No"
                            ) { _, _ -> }
                    val alert: AlertDialog? = alertDialog.create()
                    alert?.setCanceledOnTouchOutside(true)
                    alert?.show()


//                    val i = Intent(ctname.context,CaretakerDetails::class.java)
//                    i.putExtra("ctname",model.cname)
//                    i.putExtra("ctemail",model.cemail)
//
//                    //need to setflags to jump recyclerview to new tasks
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    startActivity(i)
                }

            }


        }
        caretaker_recyclerview?.adapter = adapter
        caretaker_recyclerview?.layoutManager = LinearLayoutManager(this.context)
        caretaker_recyclerview?.overScrollMode = View.OVER_SCROLL_NEVER
        adapter.notifyDataSetChanged()
        Handler().postDelayed({
            checkcaretaker(adapter)

        },1000)

    }

    private fun checkcaretaker(adapter: FirestoreRecyclerAdapter<Caretakerr, CaretakerViewHolder>) {
        if (adapter.itemCount == 0){
            noct?.visibility=View.VISIBLE
        }
        else{
            noct?.visibility = View.INVISIBLE
        }
    }


    private fun showprofile(){
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get().addOnSuccessListener { documentSnapshot ->
                var pname: String
                var pemail: String
                if (documentSnapshot.exists()) {
                    pname = documentSnapshot.getString("pname")!!
                    pemail = documentSnapshot.getString("pemail")!!
                    //Log.d("Main", "Values $puid $pname")


                    //Displaying stuff
                    usrname?.setText("${usrname.text} ${pname}")
                    usremail?.setText("${usremail.text} $pemail")
                    profile_progress_circular?.visibility = View.GONE
                    profilepic?.visibility = View.VISIBLE
                    profilecaretaker?.visibility = View.VISIBLE
                    profilesignout?.visibility = View.VISIBLE
                    usrname?.visibility = View.VISIBLE
                    usremail?.visibility = View.VISIBLE
                    editprof?.visibility = View.VISIBLE
                    profileinvite?.visibility = View.VISIBLE



                } else {
                    Toast.makeText(
                        context,
                        "Document does not exist. Contact support",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }.addOnFailureListener { e ->
                val error = e.message
                Toast.makeText(context, "Error:" + error, Toast.LENGTH_LONG).show()
            }
    }

}