package com.example.e_hospitalappointmentmanagementapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor
import com.example.e_hospitalappointmentmanagementapp.classes.Person

class docappointmentmanage : AppCompatActivity() {

    private var personId: Int = -1
    private var appointmentId: String? = null
    private lateinit var statusSpinner: Spinner
    private var initialStatus: String? = null

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

        // Initialize the status spinner
        statusSpinner = findViewById(R.id.a_status)

        // Load and display appointment details
        loadAppointmentDetails()

        // Set up button actions
        findViewById<Button>(R.id.a_remove).setOnClickListener { removeAppointment() }

        // Set up spinner change listener
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = statusSpinner.selectedItem.toString()
                if (selectedStatus != initialStatus) {
                    updateAppointmentStatus(selectedStatus)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun loadAppointmentDetails() {
        val doctor = Doctor(this)

        // Fetch the specific appointment using personId and appointmentId
        val appointment = doctor.getAppointmentDetails(personId, appointmentId!!)

        if (appointment != null) {
            findViewById<TextView>(R.id.textView31).text = "Appointment ID: ${appointment["APPOINMENT_ID"]}"
            findViewById<EditText>(R.id.a_appointmentdate).setText(appointment["APPOINMENT_DATE"])
            findViewById<EditText>(R.id.a_booked).setText(appointment["BOOKED_DATE"])
//            findViewById<EditText>(R.id.a_pay).setText(appointment["PAYMENT"])
            findViewById<EditText>(R.id.a_pay).setText("2000")
            findViewById<EditText>(R.id.a_feedback).setText(appointment["FEEDBACK"])

            // Populate the spinner with status values
            val statuses = listOf("Pending", "Completed", "Cancelled") // Add your possible statuses here
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statuses)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            statusSpinner.adapter = adapter

            // Set the spinner to the current status
            initialStatus = appointment["STATUS"] ?: "Pending"
            val statusIndex = statuses.indexOf(initialStatus)
            if (statusIndex != -1) {
                statusSpinner.setSelection(statusIndex)
            }
        } else {
            findViewById<TextView>(R.id.textView31).text = "No details available for this appointment."
            Toast.makeText(this, "Failed to load appointment details.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAppointmentStatus(newStatus: String) {
        Person.VibrationUtil.triggerVibrationshort(this)
        if (appointmentId != null) {
            val doctor = Doctor(this)

            Log.d("docappointmentmanage", "Updating appointment status to: $newStatus for ID: $appointmentId")

            val isUpdated = doctor.updateAppointmentStatus(appointmentId!!.toInt(), newStatus)

            if (isUpdated) {
                Person.VibrationUtil.triggerVibrationshort(this)
                Toast.makeText(this, "Appointment status updated to $newStatus.", Toast.LENGTH_SHORT).show()
                navigateBackToAppointments()
            } else {
                Person.VibrationUtil.triggerVibration(this)

                Toast.makeText(this, "Failed to update appointment status.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid Appointment ID.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateBackToAppointments() {
        val intent = Intent(this, docappointments::class.java) // Replace with the actual class name for the appointments activity
        intent.putExtra("person_id", personId) // Pass personId through the intent
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun removeAppointment() {
        Person.VibrationUtil.triggerVibration(this)
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
                Person.VibrationUtil.triggerVibration(this)
                Toast.makeText(this, "Failed to remove appointment.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Invalid Appointment ID.", Toast.LENGTH_SHORT).show()
        }
    }
}
