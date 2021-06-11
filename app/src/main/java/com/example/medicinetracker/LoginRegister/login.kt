package com.example.medicinetracker.LoginRegister

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.medicinetracker.LauncherScreen
import com.example.medicinetracker.MainActivity
import com.example.medicinetracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progress_circular?.visibility = View.GONE
        logregbtn12.setOnClickListener {
            progress_circular.visibility = View.VISIBLE
            val email = logemailid.text.toString()
            val pass = logpassword.text.toString()
            if (email.isEmpty() or pass.isEmpty()) {
                Toast.makeText(this, "Email or pass empty", Toast.LENGTH_SHORT).show()
                progress_circular.visibility = View.GONE
                return@setOnClickListener
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    //else if successful
                    Log.d("Main", "successfully logged in user uid: ${it.result?.user?.uid}")

                    //Dynamic Link
                    Firebase.dynamicLinks
                        .getDynamicLink(intent)
                        .addOnSuccessListener(this) { pendingDynamicLinkData ->
                            // Get deep link from result (may be null if no link is found)
                            var deepLink: Uri? = null
                            Log.d("Main","pending: $pendingDynamicLinkData")
                            Log.d("Main","pending: $deepLink")

                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.link
                                Log.d("Main","deeplink: ${deepLink.toString()}")

                            }
                            // val user = Firebase.auth.currentUser
                            if (deepLink != null) {
                                if (deepLink.getBooleanQueryParameter("invitedby", false)
                                    and deepLink.getBooleanQueryParameter("uemail",false)
                                    and deepLink.getBooleanQueryParameter("uname",false)) {
                                    //Log.d("Main","Link: ${deepLink.toString()}")
                                    val referrerUid = deepLink.getQueryParameter("invitedby")
                                    val referrerEmail = deepLink.getQueryParameter("uemail")
                                    val referrerName = deepLink.getQueryParameter("uname")
                                    Log.d("Main","Email: $referrerEmail")
                                    // Log.d("Main","Link: ${referrerUid.toString()}")

                                    //Save to patient and cretaker db
                                    createAnonymousAccountWithReferrerInfo(referrerUid,referrerEmail,referrerName)
                                    createCaretakerDB(referrerUid, referrerEmail)

                                }
                                else{
                                    Toast.makeText(this,"Badly Formatted Invite Link",Toast.LENGTH_SHORT).show()
                                }
                            }
                            //Log.d("Main","Reaching here")
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Not found: ${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    progress_circular.visibility = View.GONE

                    Toast.makeText(this, "successfully logged in", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainActivity::class.java))

                }
                .addOnFailureListener {
                    Log.d("Main", "Failed to log in user: ${it.message}")
                    Toast.makeText(this, "Failed to log in user: ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                    progress_circular.visibility=View.GONE
                }


        }
    }

    private fun createCaretakerDB(referrerUid: String?, referrerEmail: String?) {
        //val cname:String = intent?.getStringExtra("uname").toString()
        val user = Firebase.auth.currentUser
        val ctdoc: String =user?.uid.toString()+referrerUid.toString()
        val ct = hashMapOf(
            "puid" to referrerUid,
            "cuid" to user?.uid.toString(),
            "pemail" to referrerEmail.toString(),
            "cemail" to user?.email.toString(),
            "cname" to user?.displayName.toString()
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("caretakers").document(ctdoc)
            .set(ct)
            .addOnSuccessListener { //Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                 }
            .addOnFailureListener {
                Toast.makeText(this, "Error adding: ${it.message}", Toast.LENGTH_SHORT).show()
            }




    }

    private fun createAnonymousAccountWithReferrerInfo(
        referrerUid: String?,
        referrerEmail: String?,
        referrerName: String?
    ) {
        //val cname:String = intent?.getStringExtra("uname").toString()

        val user = Firebase.auth.currentUser
        val ptdoc : String =referrerUid.toString()+user?.uid.toString()
        val ct = hashMapOf(
            "puid" to referrerUid,
            "cuid" to user?.uid.toString(),
            "cemail" to user?.email.toString(),
            "cname" to user?.displayName.toString(),
            "pemail" to referrerEmail,
            "pname" to referrerName
        )
        val db = FirebaseFirestore.getInstance()
        db.collection("patient").document(ptdoc)
            .set(ct)
            .addOnSuccessListener { Toast.makeText(this, "Added patient: $referrerName", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener {
                Toast.makeText(this, "Error dding: ${it.message}", Toast.LENGTH_SHORT).show()
            }



    }

    fun signup(view: View) {
        startActivity(Intent(this, register::class.java))
    }

    fun gotosplash(view: View) {
        startActivity(Intent(this, LauncherScreen::class.java))
    }

    fun forgotpass(view: View) {
        val email = logemailid.text.toString()
        if(email.isEmpty()){
            toast("Please enter your email in the above field\nand try again")
        }
        else{
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener{
                    task->
                    if(task.isSuccessful){
                        toast("Check your mail inbox for password reset email")
                    }

                }
                .addOnFailureListener{
                    toast("Error: User not found. \nPlease create an account first! Or contact support")

                }
        }

    }


}