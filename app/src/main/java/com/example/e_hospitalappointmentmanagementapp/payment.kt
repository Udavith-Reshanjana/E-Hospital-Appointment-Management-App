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

class payment : Fragment() {

    private lateinit var payButton: Button
    private lateinit var payAmountEditText: EditText
    private lateinit var patient: Patient

    private var doctorId: Int = -1
    private var patientId: Int = -1
    private var appointmentDate: String? = null
    private var hospitalId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_payment, container, false)

        // Initialize views
        payButton = rootView.findViewById(R.id.signupbutton)
        payAmountEditText = rootView.findViewById(R.id.payamount)

        // Initialize Patient class
        patient = Patient(requireContext())

        // Retrieve data from arguments
        doctorId = arguments?.getInt("doctor_id") ?: -1
        patientId = arguments?.getInt("person_id") ?: -1
        appointmentDate = arguments?.getString("selected_date")
        hospitalId = arguments?.getInt("hospital") ?: -1

        // Set up pay button click listener
        payButton.setOnClickListener {
            if (validateInputData()) {
                processPayment()
            } else {
                Toast.makeText(requireContext(), "Invalid data. Cannot proceed with payment.", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    private fun validateInputData(): Boolean {
        return doctorId != -1 && patientId != -1 && !appointmentDate.isNullOrEmpty() && hospitalId != -1
    }

    private fun processPayment() {
        // Insert into APPOINTMENT table
        val appointmentId = insertAppointment()

        if (appointmentId == -1) {
            Toast.makeText(requireContext(), "Failed to create appointment.", Toast.LENGTH_SHORT).show()
            return
        }

        // Insert into PATIENT_APPOINTMENT table
        val patientAppointmentSuccess = insertPatientAppointment(appointmentId)
        if (!patientAppointmentSuccess) {
            Toast.makeText(requireContext(), "Failed to link patient to appointment.", Toast.LENGTH_SHORT).show()
            return
        }

        // Insert into PAYMENT table
        val paymentId = insertPayment()
        if (paymentId == -1) {
            Toast.makeText(requireContext(), "Failed to process payment.", Toast.LENGTH_SHORT).show()
            return
        }

        // Navigate to Paycomplete fragment
        navigateToPayComplete(appointmentId, paymentId)
    }

    private fun insertAppointment(): Int {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return patient.insertAppointment(
            doctorId = doctorId,
            appointmentDate = appointmentDate!!,
            bookedDate = currentDate,
            status = "Pending",
            feedback = ""
        )
    }

    private fun insertPatientAppointment(appointmentId: Int): Boolean {
        return patient.insertPatientAppointment(patientId, appointmentId)
    }

    private fun insertPayment(): Int {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val amount = payAmountEditText.text.toString().toDoubleOrNull() ?: 0.0

        return patient.insertPayment(
            paymentDate = currentDate,
            paymentTime = currentTime,
            amount = amount
        )
    }

    private fun navigateToPayComplete(appointmentId: Int, paymentId: Int) {
        val bundle = Bundle().apply {
            putInt("appointment_id", appointmentId)
            putString("appointment_date", appointmentDate)
            putInt("hospital_id", hospitalId)
            putInt("doctor_id", doctorId)
            putInt("payment_id", paymentId)
            putInt("patient_id", patientId)
        }

        val payCompleteFragment = Paycomplete()
        payCompleteFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentviewer, payCompleteFragment)
            .addToBackStack(null)
            .commit()
    }
}
