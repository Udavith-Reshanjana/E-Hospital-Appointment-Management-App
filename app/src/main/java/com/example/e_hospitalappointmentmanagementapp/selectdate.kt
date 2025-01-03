package com.example.e_hospitalappointmentmanagementapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.e_hospitalappointmentmanagementapp.classes.Patient
import java.text.SimpleDateFormat
import java.util.*

class selectdate : Fragment() {

    private lateinit var timeListView: ListView
    private lateinit var appointmentDateEditText: EditText
    private lateinit var proceedButton: Button
    private lateinit var availableDays: List<String>
    private lateinit var patient: Patient

    private var selectedDay: String? = null
    private var selectedDate: String? = null
    private var doctorId: Int = -1
    private var personId: Int = -1
    private var hospital: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_selectdate, container, false)

        // Initialize views
        timeListView = rootView.findViewById(R.id.timelist)
        appointmentDateEditText = rootView.findViewById(R.id.appointmentDate)
        proceedButton = rootView.findViewById(R.id.proceedToPayBtn)

        // Initialize Patient class
        patient = Patient(requireContext())

        // Get arguments
        doctorId = arguments?.getInt("doctor_id") ?: -1
        personId = arguments?.getInt("person_id") ?: -1
        hospital = arguments?.getString("hospital") ?: ""

        if (doctorId == -1 || personId == -1) {
            Toast.makeText(requireContext(), "Invalid doctor or patient ID.", Toast.LENGTH_SHORT).show()
            return rootView
        }

        // Retrieve available days and times
        availableDays = patient.getDoctorAvailableDays(doctorId)

        populateAvailableDays()

        // Set up appointment date picker
        appointmentDateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Handle proceed button click
        proceedButton.setOnClickListener {
            validateAndProceed()
        }

        return rootView
    }

    /**
     * Populates the ListView with available days for the doctor.
     */
    private fun populateAvailableDays() {
        if (availableDays.isNotEmpty()) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, availableDays)
            timeListView.adapter = adapter
            timeListView.setOnItemClickListener { _, _, position, _ ->
                selectedDay = availableDays[position]
                Toast.makeText(requireContext(), "Selected day: $selectedDay", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No available days for this doctor.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shows a date picker dialog for selecting an appointment date.
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                selectedDate = sdf.format(selectedCalendar.time)
                appointmentDateEditText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    /**
     * Validates user actions and navigates to the payment fragment if valid.
     */
    private fun validateAndProceed() {
        if (selectedDay == null) {
            Toast.makeText(requireContext(), "Please select a day.", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate == null) {
            Toast.makeText(requireContext(), "Please select an appointment date.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isDateMatchingDay(selectedDate!!, selectedDay!!)) {
            Toast.makeText(requireContext(), "Selected date does not match the available day.", Toast.LENGTH_SHORT).show()
            return
        }

        navigateToPaymentFragment()
    }

    /**
     * Checks if the selected date matches the doctor's available day.
     */
    private fun isDateMatchingDay(date: String, day: String): Boolean {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = sdf.parse(date)!!

        val dayOfWeek = dateCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        return dayOfWeek.equals(day, ignoreCase = true)
    }

    /**
     * Navigates to the payment fragment with the required details.
     */
    private fun navigateToPaymentFragment() {
        val bundle = Bundle().apply {
            putInt("doctor_id", doctorId)
            putInt("person_id", personId)
            putString("selected_day", selectedDay)
            putString("hospital_name", hospital)
            putString("selected_date", selectedDate)
        }
        val paymentFragment = payment()
        paymentFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentviewer, paymentFragment)
            .addToBackStack(null)
            .commit()
    }

}
