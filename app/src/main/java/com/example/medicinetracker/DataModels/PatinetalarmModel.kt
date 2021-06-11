package com.example.medicinetracker.DataModels

import java.util.ArrayList

data class PatinetalarmModel(
    val MedName: String = "",
    //val dose: String ="",
    val type: String="",
    var alarmtext: ArrayList<String> = ArrayList()
)