package com.example.medicinetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.medicinetracker.LoginRegister.register

class LauncherScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_screen)
    }

    fun login(view: View) {
        startActivity(Intent(this, com.example.medicinetracker.LoginRegister.login::class.java))
    }
    fun getStarted(view: View) {
        startActivity(Intent(this, register::class.java))
    }
}