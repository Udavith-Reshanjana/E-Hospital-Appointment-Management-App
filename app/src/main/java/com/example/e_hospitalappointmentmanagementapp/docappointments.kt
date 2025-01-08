package com.example.e_hospitalappointmentmanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor
import com.example.e_hospitalappointmentmanagementapp.classes.Person

class docappointments : AppCompatActivity() {

    private var personID: Int? = null
    private lateinit var appointmentsListView: ListView
    private lateinit var appointments: List<Map<String, String>>

    companion object {
        const val REQUEST_CODE_MANAGE_APPOINTMENT = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docappointments)

        // Get the person ID from the intent
        personID = intent.getIntExtra("person_id", -1)

        // Initialize the ListView
        appointmentsListView = findViewById(R.id.docappointments)

        // Load and display appointments
        personID?.let {
            if (it != -1) {
                loadAppointments(it)
            } else {
                Log.e("docappointments", "Invalid person ID.")
                showErrorMessage("Invalid person ID.")
            }
        }

        // Set item click listener for ListView
        appointmentsListView.setOnItemClickListener { _, _, position, _ ->
            navigateToAppointmentManage(position)
        }
    }

    private fun loadAppointments(personId: Int) {
        try {
            // Use the Doctor class to fetch appointments
            val doctor = Doctor(this)
            appointments = doctor.getAppointmentsByDoctor(personId)

            if (appointments.isNotEmpty()) {
                val appointmentDetailsList = appointments.map { appointment ->
                    """
                        Appointment ID: ${appointment["APPOINMENT_ID"]}
                        Date: ${appointment["APPOINMENT_DATE"]}
                        Status: ${appointment["STATUS"]}
                        Booked Date: ${appointment["BOOKED_DATE"]}
                        Feedback: ${appointment["FEEDBACK"]}
                    """.trimIndent()
                }

                // Use ArrayAdapter to display appointments in the ListView
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    appointmentDetailsList
                )
                appointmentsListView.adapter = adapter
            } else {
                showErrorMessage("No appointments found.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("docappointments", "Error loading appointments: ${e.message}")
            showErrorMessage("Error loading appointments.")
        }
    }

    private fun navigateToAppointmentManage(position: Int) {
        Person.VibrationUtil.triggerVibrationshort(this)
        try {
            val selectedAppointment = appointments[position]
            val appointmentId = selectedAppointment["APPOINMENT_ID"]

            if (appointmentId != null && personID != null) {
                val intent = Intent(this, docappointmentmanage::class.java).apply {
                    val bundle = Bundle().apply {
                        putInt("person_id", personID!!)
                        putString("appointment_id", appointmentId)
                    }
                    putExtras(bundle)
                }
                startActivityForResult(intent, REQUEST_CODE_MANAGE_APPOINTMENT)
            } else {
                Log.e("docappointments", "Invalid appointment or person ID.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("docappointments", "Error navigating to appointment manage: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_MANAGE_APPOINTMENT && resultCode == RESULT_OK) {
            // Reload appointments after a successful operation in docappointmentmanage
            personID?.let { loadAppointments(it) }
        }
    }

    private fun showErrorMessage(message: String) {
        Log.e("docappointments", message)
    }
}
