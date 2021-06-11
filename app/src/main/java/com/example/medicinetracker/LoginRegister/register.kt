package com.example.medicinetracker.LoginRegister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Toast
import com.example.medicinetracker.LauncherScreen
import com.example.medicinetracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*


class register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        reg_progress_circular.visibility = View.GONE

        regbtn12.setOnClickListener{

            performregister()

//            val intent = Intent (this, MainActivity::class.java)
//            startActivity(intent)
        }
        alreadylog.setOnClickListener{
            val intent1 = Intent (this, login::class.java)
            //intent1.putExtra("uname",uname.toString())
            startActivity(intent1)
        }

    }


    private fun performregister(){
        reg_progress_circular.visibility = View.VISIBLE
        val email = emailid.text.toString()
        val pass = password.text.toString()
        if (email.isEmpty() or pass.isEmpty()) {
            Toast.makeText(this,"Email or pass empty", Toast.LENGTH_SHORT).show()
            return
        }

        //Firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                //else if successful
                Log.d("Main","successfully added user uid: ${it.result?.user?.uid}")



                //add patient entry to database
                addUserToFirestore()



                reg_progress_circular.visibility = View.GONE
                Toast.makeText(this,"successfully registered", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, login::class.java))

            }
            .addOnFailureListener{
                Log.d("Main","Failed to create user: ${it.message}" )
                reg_progress_circular.visibility = View.GONE
                Toast.makeText(this,"Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun addUserToFirestore(){
        val uname = txt1.text.toString()
        displayname(uname)
        val ref = FirebaseAuth.getInstance().uid
        val city = hashMapOf(
            "pname" to uname,
            "pemail" to emailid.text.toString(),
            "puid" to ref.toString()
        )
        val db=FirebaseFirestore.getInstance()
        db.collection("users").document(ref.toString())
            .set(city)
            .addOnSuccessListener{
                Log.d("Main", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener {
                Log.w("Main", "Error writing document${it.message}")

            }
//        Firebase.dynamicLinks
//            .getDynamicLink(intent)
//            .addOnSuccessListener { pendingDynamicLinkData ->
//                // Get deep link from result (may be null if no link is found)
//
//                if (pendingDynamicLinkData != null) {
//                    //Log.d("Main","Its working bish")
//                    //Now add to caretaker page
//                    val caretakerdb = hashMapOf(
//                        "pname" to uname,
//                        "pemail" to emailid.text.toString(),
//                        "puid" to ref.toString()
//                    )
//                    db.collection("caretaker").document(ref.toString())
//                        .set(caretakerdb)
//                        .addOnSuccessListener{
//                            Log.d("Main", "Caretaker successfully written!")
//                        }
//                        .addOnFailureListener {
//                            Log.w("Main", "Error writing document${it.message}")
//
//                        }
//
//                }
//            }
    }

    private fun displayname(uname: String) {
        val user = Firebase.auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = uname
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Main", "User profile updated with name $uname")

                }
            }

    }

    fun gotosplashagain(view: View) {
        startActivity(Intent(this,LauncherScreen::class.java))

    }

}