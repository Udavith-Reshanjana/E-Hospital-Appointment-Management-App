package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor

class docappointmentmanage : AppCompatActivity() {

    private var personId: Int = -1
    private var appointmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docappointmentmanage)

        // Retrieve the IDs passed via the intent
        personId = intent.extras?.getInt("person_id", -1) ?: -1
        appointmentId = intent.extras?.getString("appointment_id")

        // Validate IDs
        if (personId == -1 || appointmentId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid data received. Returning to previous screen.", Toast.LENGTH_SHORT).show()
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

        // Fetch the specific appointment using personId and appointmentId
        val appointment = doctor.getAppointmentDetails(personId, appointmentId!!)

        if (appointment != null) {
            findViewById<TextView>(R.id.textView31).text = "Appointment ID: ${appointment["APPOINMENT_ID"]}"
            findViewById<EditText>(R.id.a_appointmentdate).setText(appointment["APPOINMENT_DATE"])
            findViewById<EditText>(R.id.a_booked).setText(appointment["BOOKED_DATE"])
            findViewById<EditText>(R.id.a_status).setText(appointment["STATUS"])
            findViewById<EditText>(R.id.a_pay).setText(appointment["PAYMENT"])
            findViewById<EditText>(R.id.a_feedback).setText(appointment["FEEDBACK"])
        } else {
            findViewById<TextView>(R.id.textView31).text = "No details available for this appointment."
            Toast.makeText(this, "Failed to load appointment details.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeAppointment() {
        if (appointmentId != null) {
            val doctor = Doctor(this)

            Log.d("docappointmentmanage", "Removing appointment with ID: $appointmentId")

            // Attempt to remove the appointment using its ID
            val isRemoved = doctor.removeAppointment(appointmentId!!.toInt())

            if (isRemoved) {
                Toast.makeText(this, "Appointment removed successfully.", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK) // Indicate success
                finish() // Close this activity
            } else {
                Toast.makeText(this, "Failed to remove appointment.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid Appointment ID.", Toast.LENGTH_SHORT).show()
        }
    }
}
