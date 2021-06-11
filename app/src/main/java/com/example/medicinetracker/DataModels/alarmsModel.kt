package com.example.medicinetracker.DataModels

import java.util.ArrayList

data class alarmsModel(
    val MedName: String = "",
    //val dose: String ="",
    val type: String="",
    var alarmtext: ArrayList<String> = ArrayList()
)