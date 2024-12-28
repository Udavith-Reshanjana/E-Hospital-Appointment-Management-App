package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.e_hospitalappointmentmanagementapp.classes.Patient

class doctorsearch : Fragment() {

    private lateinit var patient: Patient
    private lateinit var doctorsListView: ListView
    private lateinit var searchView: SearchView
    private lateinit var specializationSpinner: Spinner
    private lateinit var hospitalSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_doctorsearch, container, false)

        // Initialize UI elements
        doctorsListView = view.findViewById(R.id.doctorslist)
        searchView = view.findViewById(R.id.searchView)
        specializationSpinner = view.findViewById(R.id.spinner)
        hospitalSpinner = view.findViewById(R.id.spinner2)

        // Initialize Patient (inherits database methods from Person)
        patient = Patient(requireContext())

        // Populate initial data
        populateDoctorList()

        // Set up spinners
        setupFilterSpinners()

        // Handle search queries
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchDoctors(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isEmpty()) {
                    populateDoctorList() // Reload full list on empty query
                }
                return true
            }
        })

        // Handle doctor item clicks
        doctorsListView.setOnItemClickListener { _, _, position, _ ->
            val selectedDoctor = doctorsListView.adapter.getItem(position) as String
            val doctorDetails = selectedDoctor.split("\n")
            val doctorName = doctorDetails[0].replace("Dr. ", "")

            val doctorId = patient.getDoctorIdByName(doctorName)
            val personId = arguments?.getInt("person_id", -1) ?: -1

            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Book Appointment")
            alertDialog.setMessage("Do you want to book an appointment for Dr. $doctorName?")
            alertDialog.setPositiveButton("Yes") { _, _ ->
                navigateToDoctorBookProfile(doctorId, personId)
            }
            alertDialog.setNegativeButton("No", null)
            alertDialog.show()
        }

        return view
    }

    private fun populateDoctorList() {
        val doctors = patient.getAllDoctors()
        if (doctors.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                doctors.map {
                    "Dr. ${it["FIRST_NAME"]} ${it["LAST_NAME"]}\n" +
                            "Specialized in: ${it["DOC_SPECIALITY"]}\n" +
                            "Email: ${it["EMAIL"]}\n" +
                            "Available: ${it["AVAILABILITY"]}\n" +
                            "Hospitals: ${it["HOSPITALS"]}"
                }
            )
            doctorsListView.adapter = adapter
        } else {
            Toast.makeText(requireContext(), "No doctors available.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchDoctors(query: String) {
        val filteredDoctors = patient.searchDoctors(query)
        if (filteredDoctors.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                filteredDoctors.map {
                    "Dr. ${it["FIRST_NAME"]} ${it["LAST_NAME"]}\n" +
                            "Specialized in: ${it["DOC_SPECIALITY"]}\n" +
                            "Email: ${it["EMAIL"]}\n" +
                            "Available: ${it["AVAILABILITY"]}\n" +
                            "Hospitals: ${it["HOSPITALS"]}"
                }
            )
            doctorsListView.adapter = adapter
        } else {
            Toast.makeText(requireContext(), "No doctors found for \"$query\".", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFilterSpinners() {
        val specializations = patient.getAllSpecializations()
        val hospitals = patient.getAllHospitals()

        val specializationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Specialization") + specializations)
        specializationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        specializationSpinner.adapter = specializationAdapter

        val hospitalAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listOf("Select Hospital") + hospitals)
        hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hospitalSpinner.adapter = hospitalAdapter

        specializationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) filterDoctorsBySpecializationAndHospital() // Ignore the first "Select" option
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        hospitalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) filterDoctorsBySpecializationAndHospital() // Ignore the first "Select" option
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterDoctorsBySpecializationAndHospital() {
        val specialization = specializationSpinner.selectedItem.toString()
        val hospital = hospitalSpinner.selectedItem.toString()

        if (specialization == "Select Specialization" && hospital == "Select Hospital") {
            populateDoctorList()
            return
        }

        val filteredDoctors = patient.filterDoctorsBySpecializationAndHospital(specialization, hospital)
        if (filteredDoctors.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                filteredDoctors.map {
                    "Dr. ${it["FIRST_NAME"]} ${it["LAST_NAME"]}\n" +
                            "Specialized in: ${it["DOC_SPECIALITY"]}\n" +
                            "Email: ${it["EMAIL"]}\n" +
                            "Available: ${it["AVAILABILITY"]}\n" +
                            "Hospitals: ${it["HOSPITALS"]}"
                }
            )
            doctorsListView.adapter = adapter
        } else {
            Toast.makeText(requireContext(), "No doctors match the selected filters.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDoctorBookProfile(doctorId: Int, personId: Int) {
        val bundle = Bundle().apply {
            putInt("doctor_id", doctorId)
            putInt("person_id", personId)
        }
        val doctorBookProfileFragment = doctorbookprofile()
        doctorBookProfileFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentviewer, doctorBookProfileFragment)
            .addToBackStack(null)
            .commit()
    }
}
