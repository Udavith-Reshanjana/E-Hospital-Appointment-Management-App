package com.example.e_hospitalappointmentmanagementapp

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.e_hospitalappointmentmanagementapp.classes.Patient
import com.example.e_hospitalappointmentmanagementapp.classes.Person.VibrationUtil.triggerVibration
import com.example.e_hospitalappointmentmanagementapp.classes.Person.VibrationUtil.triggerVibrationshort


class doctorbookprofile : Fragment() {

    private lateinit var doctorImageButton: ImageButton
    private lateinit var fullNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var specializationTextView: TextView
    private lateinit var hospitalSpinner: Spinner
    private lateinit var bookAppointmentButton: Button
    private lateinit var discardButton: Button
    private lateinit var patient: Patient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_doctorbookprofile, container, false)

        // Initialize views
        doctorImageButton = rootView.findViewById(R.id.imageButton)
        fullNameTextView = rootView.findViewById(R.id.profilename)
        emailTextView = rootView.findViewById(R.id.profileemail)
        specializationTextView = rootView.findViewById(R.id.Specialize)
        hospitalSpinner = rootView.findViewById(R.id.spinner3)
        bookAppointmentButton = rootView.findViewById(R.id.logoutbutton)
        discardButton = rootView.findViewById(R.id.discardbutton)

        // Initialize Patient class
        patient = Patient(requireContext())

        // Get arguments passed to the fragment
        val doctorId = arguments?.getInt("doctor_id") ?: -1
        val patientId = arguments?.getInt("person_id") ?: -1

        // Validate doctor ID and populate UI
        if (doctorId != -1) {
            populateDoctorUI(doctorId)
        } else {
            Toast.makeText(requireContext(), "Invalid doctor ID.", Toast.LENGTH_SHORT).show()
        }

        // Handle "Book an Appointment" button
        bookAppointmentButton.setOnClickListener {
            val selectedHospital = hospitalSpinner.selectedItem?.toString()
            if (selectedHospital == null || selectedHospital == "Select Hospital") {
                triggerVibration(requireContext())
                Toast.makeText(requireContext(), "Please select a hospital.", Toast.LENGTH_SHORT).show()
            } else {
                triggerVibrationshort(requireContext())
                navigateToSelectDate(doctorId, patientId, selectedHospital)
            }
        }

        // Handle "Discard Appointment" button
        discardButton.setOnClickListener {
            showDiscardConfirmation()
            triggerVibration(requireContext())
        }

        return rootView
    }

    /**
     * Populates the UI with doctor details.
     */
    private fun populateDoctorUI(doctorId: Int) {
        val doctorProfile = patient.getDoctorById(doctorId)

        doctorProfile?.let {
            // Update TextViews with doctor details
            val fullName = "${it["FIRST_NAME"] ?: ""} ${it["LAST_NAME"] ?: ""}"
            fullNameTextView.text = fullName
            emailTextView.text = (it["EMAIL"] ?: "") as CharSequence?
            specializationTextView.text = (it["DOC_SPECIALITY"] ?: "") as CharSequence?

            // Update ImageButton with profile picture
            val profilePic = it["PROFILE_PIC"] as ByteArray?
            if (profilePic != null && profilePic.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(profilePic, 0, profilePic.size)
                doctorImageButton.setImageBitmap(bitmap)
            } else {
                doctorImageButton.setImageDrawable(null)
                doctorImageButton.contentDescription = "No Image Available"
                Toast.makeText(requireContext(), "No profile picture available.", Toast.LENGTH_SHORT).show()
            }

            // Populate the hospital spinner
            populateHospitalSpinner(doctorId)
        } ?: run {
            Toast.makeText(requireContext(), "Failed to load doctor profile. Doctor not found.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Populates the hospital spinner with hospitals associated with the doctor.
     */
    private fun populateHospitalSpinner(doctorId: Int) {
        val hospitals = patient.getHospitalsByDoctorId(doctorId)
        if (hospitals.isEmpty()) {
            Toast.makeText(requireContext(), "No hospitals found for this doctor.", Toast.LENGTH_SHORT).show()
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Select Hospital") + hospitals
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hospitalSpinner.adapter = adapter
    }

    /**
     * Navigates to the `selectdate` fragment with doctor, patient, and hospital details.
     */
    private fun navigateToSelectDate(doctorId: Int, patientId: Int, hospital: String) {
        val bundle = Bundle().apply {
            putInt("doctor_id", doctorId)
            putInt("person_id", patientId)
            putString("hospital", hospital)
        }
        val selectDateFragment = selectdate()
        selectDateFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentviewer, selectDateFragment)
            .addToBackStack(null) // Ensure back stack is maintained
            .commit()
    }

    /**
     * Shows a confirmation dialog to discard the appointment and navigate back to `doctorsearch`.
     */
    private fun showDiscardConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Discard Appointment")
            .setMessage("Are you sure you want to discard this appointment?")
            .setPositiveButton("Yes") { _, _ ->
                navigateBackWithBundle()
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    private fun navigateBackWithBundle() {
        val doctorSearchFragment = doctorsearch()

        // Pass relevant data via bundle
        val bundle = Bundle().apply {
            putString("message", "Appointment discarded successfully.")
            // Add other data if needed
        }
        doctorSearchFragment.arguments = bundle

        // Navigate back without relying on the back stack
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentviewer, doctorSearchFragment)
            .commit()
    }


}
