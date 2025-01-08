package com.example.e_hospitalappointmentmanagementapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.e_hospitalappointmentmanagementapp.classes.Person
import com.example.e_hospitalappointmentmanagementapp.classes.Person.VibrationUtil.triggerVibration
import com.example.e_hospitalappointmentmanagementapp.classes.Person.VibrationUtil.triggerVibrationshort

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
            putExtra("firstName", doctorFirstName)
        }
        startActivity(intent)
    }

    fun gotoDocAppoinmentManagement(view: View) {
        triggerVibrationshort(this)
        gotoAnyScreen(docappointments::class.java)
    }

    fun gotoDocAvailabilityManagement(view: View) {
        gotoAnyScreen(docavailable::class.java)
        triggerVibrationshort(this)
    }

    fun gotoDocProfile(view: View) {
        gotoAnyScreen(docprofile::class.java)
        triggerVibrationshort(this)
    }

    fun logout(view: View) {
        Person.VibrationUtil.triggerVibration(this)
        AlertDialog.Builder(this)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, Login::class.java)
                triggerVibration(this)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
