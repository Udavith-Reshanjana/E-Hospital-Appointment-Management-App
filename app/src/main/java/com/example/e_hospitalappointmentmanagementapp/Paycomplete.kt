package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.e_hospitalappointmentmanagementapp.classes.Patient

class Paycomplete : Fragment() {

    private lateinit var appointmentIdEditText: EditText
    private lateinit var appointmentDateEditText: EditText
    private lateinit var appointmentAtEditText: EditText
    private lateinit var appointmentTimeEditText: EditText
    private lateinit var returnButton: Button
    private lateinit var patient: Patient

    private var doctorId: Int = -1
    private var patientId: Int = -1
    private var appointmentId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_paycomplete, container, false)

        // Initialize views
        appointmentIdEditText = rootView.findViewById(R.id.appointmentid)
        appointmentDateEditText = rootView.findViewById(R.id.appointmentdate)
        appointmentAtEditText = rootView.findViewById(R.id.appointmentat)
        appointmentTimeEditText = rootView.findViewById(R.id.appointmenttime)
        returnButton = rootView.findViewById(R.id.signupbutton)

        // Initialize Patient class
        patient = Patient(requireContext())

        // Retrieve data from the bundle
        doctorId = arguments?.getInt("doctor_id") ?: -1
        patientId = arguments?.getInt("patient_id") ?: -1
        appointmentId = arguments?.getInt("appointment_id") ?: -1

        val appointmentDate = arguments?.getString("appointment_date") ?: "N/A"

        if (doctorId == -1 || patientId == -1 || appointmentId == -1) {
            Toast.makeText(requireContext(), "Invalid data. Cannot load details.", Toast.LENGTH_SHORT).show()
            return rootView
        }

        // Populate the appointment details
        appointmentIdEditText.setText(appointmentId.toString())
        appointmentDateEditText.setText(appointmentDate)

        // Retrieve and display hospital name and available time
        populateHospitalAndTime()

        // Handle return button click
        returnButton.setOnClickListener {
            navigateToHomeScreen()
        }

        return rootView
    }

    /**
     * Retrieves the hospital name and doctor's available time for the appointment.
     */
    private fun populateHospitalAndTime() {
        // Fetch the hospital name using the patient class
        val hospitalName = patient.getHospitalNameByAppointment(appointmentId)
        appointmentAtEditText.setText(hospitalName ?: "N/A")

        // Fetch the doctor's available time period
        val availableTime = patient.getDoctorAvailableTimePeriod(doctorId)
        appointmentTimeEditText.setText(availableTime ?: "N/A")
    }

    /**
     * Navigates to the home screen, passing the patient ID through a bundle.
     */
    private fun navigateToHomeScreen() {
        val homeFragment = Home()
        val bundle = Bundle().apply {
            putInt("person_id", patientId)
        }
        homeFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentviewer, homeFragment)
            .commit()
    }
}
