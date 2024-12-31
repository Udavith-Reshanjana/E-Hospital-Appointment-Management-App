package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor

class docappointments : AppCompatActivity() {

    private var personID: Int? = null
    private lateinit var appointmentsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docappointments)

        // Get the person ID from the intent
        personID = intent.getIntExtra("person_id", -1)

        // Initialize the LinearLayout inside the ScrollView
        appointmentsContainer = findViewById(R.id.docappointments)

        // Load and display appointments
        personID?.let {
            if (it != -1) {
                loadAppointments(it)
            } else {
                Log.e("docappointments", "Invalid person ID.")
            }
        }
    }

    private fun loadAppointments(personId: Int) {
        try {
            // Use the Doctor class to fetch appointments
            val doctor = Doctor(this)
            val appointments = doctor.getAppointmentsByDoctor(personId)

            if (appointments.isNotEmpty()) {
                for (appointment in appointments) {
                    val appointmentDetails = """
                        Appointment ID: ${appointment["APPOINMENT_ID"]}
                        Date: ${appointment["APPOINMENT_DATE"]}
                        Status: ${appointment["STATUS"]}
                        Booked Date: ${appointment["BOOKED_DATE"]}
                        Feedback: ${appointment["FEEDBACK"]}
                        Payment: ${appointment["PAYMENT"]}
                    """.trimIndent()

                    // Create and add TextView for each appointment
                    val textView = TextView(this).apply {
                        text = appointmentDetails
                        textSize = 16f
                        setPadding(16, 16, 16, 16)
                    }
                    appointmentsContainer.addView(textView)
                }
            } else {
                // Show "No appointments found" message
                val noDataTextView = TextView(this).apply {
                    text = "No appointments found."
                    textSize = 18f
                    setPadding(16, 16, 16, 16)
                }
                appointmentsContainer.addView(noDataTextView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("docappointments", "Error loading appointments: ${e.message}")
        }
    }
}
