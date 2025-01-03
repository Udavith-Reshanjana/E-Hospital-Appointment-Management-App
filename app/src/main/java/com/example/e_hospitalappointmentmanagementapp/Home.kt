package com.example.e_hospitalappointmentmanagementapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Retrieve the arguments passed from the Activity
        val patientFName = arguments?.getString("first_name")
        val personId = arguments?.getInt("person_id", -1)

        // Update the welcome message with the patient's first name
        val welcomeText = view.findViewById<TextView>(R.id.patientFirstName)
        welcomeText.text = patientFName

        // Set up buttons
        val bookAppointmentButton = view.findViewById<Button>(R.id.bookappointmentbutton)
        val myAppointmentsButton = view.findViewById<Button>(R.id.myappointmentbutton)
        val logoutButton = view.findViewById<Button>(R.id.homelogout)

        // Handle "Book Appointment" button click
        bookAppointmentButton.setOnClickListener {
            val bookAppointmentFragment = doctorsearch()
            bookAppointmentFragment.arguments = Bundle().apply {
                putInt("person_id", personId ?: -1)
            }
            loadFragment(bookAppointmentFragment)
        }

        // Handle "My Appointments" button click
        myAppointmentsButton.setOnClickListener {
            val myAppointmentsFragment = myappointments()
            myAppointmentsFragment.arguments = Bundle().apply {
                putInt("person_id", personId ?: -1)
            }
            loadFragment(myAppointmentsFragment)
        }

        // Handle "Log Out" button click
        logoutButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ ->
                    val intent = Intent(requireContext(), Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        return view
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentviewer, fragment)
            .addToBackStack(null) // Add transaction to the back stack to allow navigation back
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
