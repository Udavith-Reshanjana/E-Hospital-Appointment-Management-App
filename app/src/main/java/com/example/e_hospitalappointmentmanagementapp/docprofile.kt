package com.example.e_hospitalappointmentmanagementapp

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor
import java.io.ByteArrayOutputStream

class docprofile : AppCompatActivity() {

    private var personId: Int = -1
    private lateinit var doctor: Doctor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docprofile)

        personId = intent.getIntExtra("person_id", -1)
        val first_name = intent.getStringExtra("firstName")

        findViewById<TextView>(R.id.docUsername).text = first_name

        if (personId == -1) {
            Toast.makeText(this, "Invalid person ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        doctor = Doctor(this)
        loadProfileData()
    }

    fun logout(view: View) {
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun updateProfile(view: View) {
        val proPic = findViewById<ImageButton>(R.id.propic)
        val fName = findViewById<EditText>(R.id.docprofilefirstname).text.toString()
        val lName = findViewById<EditText>(R.id.docprofilelastname).text.toString()
        val email = findViewById<EditText>(R.id.docprofileemail).text.toString()
        val oldPassword = findViewById<EditText>(R.id.docprofilepw).text.toString()
        val newPassword = findViewById<EditText>(R.id.docprofilepwc).text.toString()

        if (fName.isBlank() || lName.isBlank() || email.isBlank()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.isNotBlank() && newPassword == oldPassword) {
            Toast.makeText(this, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show()
            return
        }

        val success = doctor.updateProfile(
            personId,
            fName,
            lName,
            email,
            oldPassword,
            newPassword
        )

        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileData() {
        val doctorProfile = doctor.getProfile(personId)

        doctorProfile?.let {
            findViewById<EditText>(R.id.docprofilefirstname).setText(it["FIRST_NAME"] ?: "")
            findViewById<EditText>(R.id.docprofilelastname).setText(it["LAST_NAME"] ?: "")
            findViewById<EditText>(R.id.docprofileemail).setText(it["EMAIL"] ?: "")

            val hospitalList = doctor.getHospitals(personId)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hospitalList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            findViewById<Spinner>(R.id.profilehosptiallist).adapter = adapter
        }
    }




}
