package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.e_hospitalappointmentmanagementapp.classes.Patient

class notification : Fragment() {
    private var personId: Int = -1
    private lateinit var patient: Patient
    private lateinit var notificationsListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        // Retrieve personId from arguments
        personId = arguments?.getInt("person_id", -1) ?: -1
        if (personId == -1) {
            Toast.makeText(context, "Invalid person ID", Toast.LENGTH_SHORT).show()
            return view
        }

        patient = Patient(requireContext())
        notificationsListView = view.findViewById(R.id.notifications)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch and display appointment notifications
        fetchAndDisplayNotifications()
    }

    private fun fetchAndDisplayNotifications() {
        try {
            // Fetch appointments from the Patient class
            val appointments = patient.getAppointmentsByPersonId(personId)

            if (appointments.isEmpty()) {
                Toast.makeText(context, "No notifications available.", Toast.LENGTH_SHORT).show()
                return
            }

            // Convert appointments to a list of notification strings
            val notifications = appointments.map { appointment ->
                "Reminder: Appointment scheduled on ${appointment["APPOINMENT_DATE"]} - Status: ${appointment["STATUS"]}"
            }

            // Display notifications in ListView
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notifications)
            notificationsListView.adapter = adapter
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load notifications.", Toast.LENGTH_SHORT).show()
        }
    }
}
