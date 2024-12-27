package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor

class docappointmentmanage : AppCompatActivity() {

    private var docId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docappointmentmanage)

        // Retrieve the doctor's ID passed from the intent
        docId = intent.getIntExtra("person_id", -1)

        // Validate the doctor ID
        if (docId == -1) {
            Toast.makeText(this, "Invalid Doctor ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Load and display appointment details
        loadAppointmentDetails()

        // Set up button actions
        findViewById<Button>(R.id.a_remove).setOnClickListener { removeAppointment() }
        findViewById<Button>(R.id.a_back).setOnClickListener { finish() }
    }

    private fun loadAppointmentDetails() {
        val doctor = Doctor(this)

        // Fetch appointments for the doctor
        val appointments = doctor.getAppointmentsByDoctor(docId)

        // Display the first appointment (for simplicity)
        if (appointments.isNotEmpty()) {
            val appointment = appointments[0]
            findViewById<TextView>(R.id.textView31).text = "Appointment ID: ${appointment["AppointmentID"]}"
            findViewById<EditText>(R.id.a_appointmentdate).setText(appointment["AppointmentDate"])
            findViewById<EditText>(R.id.a_booked).setText(appointment["BookedOn"])
            findViewById<EditText>(R.id.a_status).setText(appointment["Status"])
            findViewById<EditText>(R.id.a_pay).setText(appointment["Payment"])
            findViewById<EditText>(R.id.a_feedback).setText(appointment["Feedback"])
        } else {
            findViewById<TextView>(R.id.textView31).text = "No appointments found."
        }
    }

    private fun removeAppointment() {
        val doctor = Doctor(this)
        val appointmentIdText = findViewById<TextView>(R.id.textView31).text.toString()
        val appointmentId = appointmentIdText.substringAfter("Appointment ID: ").toIntOrNull()

        if (appointmentId == null) {
            Toast.makeText(this, "Invalid Appointment ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Remove appointment using Doctor class method
        val isRemoved = doctor.removeAppointment(appointmentId)

        if (isRemoved) {
            Toast.makeText(this, "Appointment removed successfully.", Toast.LENGTH_SHORT).show()
            finish() // Return to previous screen
        } else {
            Toast.makeText(this, "Failed to remove appointment.", Toast.LENGTH_SHORT).show()
        }
    }
}
