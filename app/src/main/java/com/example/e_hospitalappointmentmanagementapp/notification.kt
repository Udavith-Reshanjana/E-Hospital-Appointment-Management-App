package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.util.Log
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

        // Insert and load notifications
        insertAndLoadNotifications()
    }

    private fun insertAndLoadNotifications() {
        try {
            val notifications = mutableListOf<String>()

            // Fetch appointments and add notifications
            val appointments = patient.getAppointmentsForNotifications(personId)
            for ((appointmentId, appointmentDate) in appointments) {
                if (patient.insertNotificationIfNotExists(personId, appointmentId)) {
                    Log.d("Notification", "Notification inserted for Appointment ID: $appointmentId")
                }
                notifications.add("Reminder: Appointment scheduled on $appointmentDate")
            }

            // Display notifications in ListView
            if (notifications.isEmpty()) {
                Toast.makeText(context, "No notifications available.", Toast.LENGTH_SHORT).show()
            } else {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notifications)
                notificationsListView.adapter = adapter
            }
        } catch (e: Exception) {
            Log.e("NotificationFragment", "Error loading notifications", e)
            Toast.makeText(context, "Failed to load notifications.", Toast.LENGTH_SHORT).show()
        }
    }
}
