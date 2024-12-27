package com.example.e_hospitalappointmentmanagementapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Doctormain : AppCompatActivity() {

    private var doctorPersonId: Int = -1
    private lateinit var doctorFirstName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctormain)

        doctorFirstName = intent.getStringExtra("first_name") ?: "Unknown Doctor"
        doctorPersonId = intent.getIntExtra("person_id", -1)

        val docFirstName = findViewById<TextView>(R.id.doctorUserName)
        docFirstName.text = doctorFirstName
    }

    // Function to navigate to any activity and pass the person_id
    private fun gotoAnyScreen(activityClass: Class<*>) {
        val intent = Intent(this, activityClass).apply {
            putExtra("person_id", doctorPersonId)
        }
        startActivity(intent)
    }

    fun gotoDocAppoinmentManagement(view: View) {
        gotoAnyScreen(docappointmentmanage::class.java)
    }

    fun gotoDocAvailabilityManagement(view: View) {
        gotoAnyScreen(docavailable::class.java)
    }

    fun gotoDocProfile(view: View) {
        gotoAnyScreen(docprofile::class.java)
    }

    fun logout(view: View) {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
