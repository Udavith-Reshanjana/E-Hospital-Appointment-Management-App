package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.e_hospitalappointmentmanagementapp.classes.Patient
import java.text.SimpleDateFormat
import java.util.*

class Appointment : Fragment() {

    private lateinit var appointmentDateEditText: EditText
    private lateinit var bookedDateEditText: EditText
    private lateinit var doctorNameEditText: EditText
    private lateinit var statusEditText: EditText
    private lateinit var paymentEditText: EditText
    private lateinit var feedbackEditText: EditText
    private lateinit var leaveFeedbackButton: Button
    private lateinit var backButton: Button
    private lateinit var appointmentIDTextView: TextView

    private lateinit var patient: Patient
    private var patientId: Int = -1
    private var appointmentId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_appointment, container, false)

        // Initialize views
        appointmentDateEditText = rootView.findViewById(R.id.a_appointmentdate)
        bookedDateEditText = rootView.findViewById(R.id.a_booked)
        doctorNameEditText = rootView.findViewById(R.id.a_doc)
        statusEditText = rootView.findViewById(R.id.a_status)
        paymentEditText = rootView.findViewById(R.id.a_pay)
        feedbackEditText = rootView.findViewById(R.id.a_feedback)
        leaveFeedbackButton = rootView.findViewById(R.id.leavefeedback)
        backButton = rootView.findViewById(R.id.back)
        appointmentIDTextView = rootView.findViewById(R.id.textView31)

        // Initialize Patient class
        patient = Patient(requireContext())

        // Retrieve data from arguments
        patientId = arguments?.getInt("person_id") ?: -1
        appointmentId = arguments?.getInt("appointment_id") ?: -1

        if (patientId == -1 || appointmentId == -1) {
            Toast.makeText(requireContext(), "Invalid patient or appointment ID.", Toast.LENGTH_SHORT).show()
            return rootView
        }

        // Populate appointment details
        populateAppointmentDetails()

        // Handle Leave Feedback button click
        leaveFeedbackButton.setOnClickListener {
            saveFeedback()
        }

        // Handle Back button click
        backButton.setOnClickListener {
            navigateBack()
        }

        appointmentIDTextView.text = "Appointment ID: $appointmentId"

        return rootView
    }

    /**
     * Populates appointment details from the database.
     */
    private fun populateAppointmentDetails1() {
        val appointmentDetails = patient.getAppointmentDetails(appointmentId)

        appointmentDetails?.let {
            appointmentDateEditText.setText(it["APPOINTMENT_DATE"] ?: "")
            bookedDateEditText.setText(it["BOOKED_DATE"] ?: "")
            doctorNameEditText.setText(it["DOCTOR_NAME"] ?: "")
            statusEditText.setText(it["STATUS"] ?: "")
            paymentEditText.setText(it["PAYMENT"] ?: "N/A")
            feedbackEditText.setText(it["FEEDBACK"] ?: "")
        } ?: run {
            Toast.makeText(requireContext(), "Failed to load appointment details.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateAppointmentDetails() {
        val appointmentDetails = patient.getAppointmentDetails(appointmentId)

        appointmentDetails?.let {
            appointmentDateEditText.setText(it["APPOINTMENT_DATE"] ?: "")

            // Convert and format the "Booked On" date to "dd-MM-yyyy"
            val bookedDate = it["BOOKED_DATE"]
            bookedDateEditText.setText(formatDateToDDMMYYYY(bookedDate) ?: "")

            doctorNameEditText.setText(it["DOCTOR_NAME"] ?: "")
            statusEditText.setText(it["STATUS"] ?: "")
            paymentEditText.setText(it["PAYMENT"] ?: "")
            feedbackEditText.setText(it["FEEDBACK"] ?: "")
        } ?: run {
            Toast.makeText(requireContext(), "Failed to load appointment details.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatDateToDDMMYYYY(dateString: String?): String? {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString ?: return null)
            outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    /**
     * Saves the feedback entered by the user to the database.
     */
    private fun saveFeedback() {
        val feedback = feedbackEditText.text.toString().trim()
        if (feedback.isEmpty()) {
            Toast.makeText(requireContext(), "Feedback cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val success = patient.updateAppointmentFeedback(appointmentId, feedback)
        if (success) {
            Toast.makeText(requireContext(), "Feedback saved successfully.", Toast.LENGTH_SHORT).show()
            // Navigate back to the previous fragment
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(requireContext(), "Failed to save feedback.", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Navigates back to the previous fragment.
     */
    private fun navigateBack() {
        parentFragmentManager.popBackStack()
    }
}
