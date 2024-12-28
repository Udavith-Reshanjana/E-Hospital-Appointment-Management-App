package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.e_hospitalappointmentmanagementapp.classes.Patient

class myappointments : Fragment() {

    private lateinit var patient: Patient
    private lateinit var appointmentsListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myappointments, container, false)

        // Initialize UI elements
        appointmentsListView = view.findViewById(R.id.myappointments)

        // Initialize Patient (inherits database methods from Person)
        patient = Patient(requireContext())

        // Retrieve person ID from arguments
        val personId = arguments?.getInt("person_id", -1) ?: -1

        // Load appointments for the person ID
        if (personId != -1) {
            loadAppointments(personId)
        } else {
            Toast.makeText(requireContext(), "Invalid person ID", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadAppointments(personId: Int) {
        val appointments = patient.getAppointmentsByPersonId(personId)

        if (appointments.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                appointments.map {
                    "Appointment ID: ${it["APPOINMENT_ID"]}\n" +
                            "Date: ${it["APPOINMENT_DATE"]}\n" +
                            "Status: ${it["STATUS"]}\n" +
                            "Booked On: ${it["BOOKED_DATE"]}\n" +
                            "Feedback: ${it["FEEDBACK"] ?: "No feedback"}"
                }
            )
            appointmentsListView.adapter = adapter

            // Handle appointment clicks
            appointmentsListView.setOnItemClickListener { _, _, position, _ ->
                val selectedAppointment = appointments[position]
                val appointmentId = selectedAppointment["APPOINMENT_ID"]?.toInt() ?: -1

                navigateToAppointmentFragment(personId, appointmentId)
            }
        } else {
            Toast.makeText(requireContext(), "No appointments found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToAppointmentFragment(personId: Int, appointmentId: Int) {
        val bundle = Bundle().apply {
            putInt("person_id", personId)
            putInt("appointment_id", appointmentId)
        }
        val appointmentFragment = Appointment()
        appointmentFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentviewer, appointmentFragment)
            .addToBackStack(null)
            .commit()
    }
}
