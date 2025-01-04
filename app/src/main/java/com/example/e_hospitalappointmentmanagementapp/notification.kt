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
import java.util.*

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

        // Load and display notifications
        loadAppointmentsAsNotifications()
    }

    private fun loadAppointmentsAsNotifications() {
        try {
            val appointments = patient.getAppointmentsByPersonId(personId)

            if (appointments.isEmpty()) {
                Toast.makeText(context, "No appointments available.", Toast.LENGTH_SHORT).show()
            } else {
                val notifications = appointments.map { appointment ->
                    val appointmentId = appointment["APPOINMENT_ID"]
                    val appointmentDate = appointment["APPOINMENT_DATE"]
                    val status = appointment["STATUS"]
                    val bookedDate = appointment["BOOKED_DATE"]

                    when (status?.lowercase(Locale.getDefault())) {
                        "pending" -> "Your appointment #$appointmentId is scheduled for $appointmentDate and is currently pending. Booked on $bookedDate."
                        "confirmed" -> "Reminder: Your confirmed appointment #$appointmentId is on $appointmentDate. Booked on $bookedDate."
                        "completed" -> "Thank you for attending appointment #$appointmentId on $appointmentDate. We hope to see you again!"
                        else -> "Appointment #$appointmentId is scheduled for $appointmentDate with status: $status. Booked on $bookedDate."
                    }
                }

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notifications)
                notificationsListView.adapter = adapter
            }
        } catch (e: Exception) {
            Log.e("NotificationFragment", "Error loading appointments", e)
            Toast.makeText(context, "Failed to load notifications.", Toast.LENGTH_SHORT).show()
        }
    }
}
