package com.example.e_hospitalappointmentmanagementapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Doctormain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctormain)

        val doctorFirstName = intent.getStringExtra("first_name")

        val docFirstName = findViewById<TextView>(R.id.doctorUserName)
        docFirstName.text = doctorFirstName
    }
}