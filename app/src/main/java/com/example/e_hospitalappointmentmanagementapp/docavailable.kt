package com.example.e_hospitalappointmentmanagementapp

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor
import com.example.e_hospitalappointmentmanagementapp.classes.Person
import java.util.*
import java.util.regex.Pattern

class docavailable : AppCompatActivity() {

    private var docId: Int = -1
    private lateinit var doctor: Doctor
    private lateinit var dateListView: ListView
    private lateinit var daySpinner: Spinner
    private lateinit var hospitalSpinner: Spinner
    private lateinit var fromTimeInput: EditText
    private lateinit var toTimeInput: EditText
    private lateinit var availabilityAdapter: ArrayAdapter<String>
    private val availabilityList = mutableListOf<String>()
    private var selectedItemPosition: Int = AdapterView.INVALID_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docavailable)

        doctor = Doctor(this)
        docId = intent.getIntExtra("person_id", -1)

        if (docId == -1) {
            Toast.makeText(this, "Invalid Doctor ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize UI components
        dateListView = findViewById(R.id.datelist)
        daySpinner = findViewById(R.id.availableDay)
        hospitalSpinner = findViewById(R.id.avl_hospitallist)
        fromTimeInput = findViewById(R.id.timefrom)
        toTimeInput = findViewById(R.id.timeto)

        availabilityAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, availabilityList)
        dateListView.adapter = availabilityAdapter

        dateListView.setOnItemClickListener { _, _, position, _ ->
            selectedItemPosition = position
            Person.VibrationUtil.triggerVibrationshort(this)
            Toast.makeText(this, "Selected: ${availabilityList[position]}", Toast.LENGTH_SHORT).show()
        }

        setupDaySpinner()
        loadHospitals()
        loadAvailability()

        // Set TimePickerDialog for fromTimeInput and toTimeInput
        fromTimeInput.setOnClickListener {
            Person.VibrationUtil.triggerVibrationshort(this)
            showTimePickerDialog(fromTimeInput) }
        toTimeInput.setOnClickListener {
            Person.VibrationUtil.triggerVibrationshort(this)
            showTimePickerDialog(toTimeInput) }

        findViewById<Button>(R.id.addtime).setOnClickListener { addAvailability() }
        findViewById<Button>(R.id.removetime).setOnClickListener { removeAvailability() }
    }

    private fun setupDaySpinner() {
        val days = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        daySpinner.adapter = dayAdapter
    }

    private fun loadHospitals() {
        val hospitals = doctor.getHospitalsForDoctor(docId)

        if (hospitals.isEmpty()) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "No hospitals found for the doctor.", Toast.LENGTH_SHORT).show()
            return
        }

        val hospitalAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hospitals)
        hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hospitalSpinner.adapter = hospitalAdapter
    }

    private fun loadAvailability() {
        availabilityList.clear()
        val availability = doctor.getAvailabilityForDoctor(docId)
        availability.forEach {
            val availabilityString = "${it["AVAILABLE_DAY"]} - ${it["AVAILABLE_TIME"]} to ${it["AVAILABLE_TIME_END"]}"
            availabilityList.add(availabilityString)
        }
        availabilityAdapter.notifyDataSetChanged()
    }

    private fun addAvailability() {
        val selectedDay = daySpinner.selectedItem?.toString() ?: ""
        val fromTime = fromTimeInput.text.toString()
        val toTime = toTimeInput.text.toString()
        val selectedHospital = hospitalSpinner.selectedItem?.toString() ?: ""

        if (selectedDay.isEmpty() || fromTime.isEmpty() || toTime.isEmpty() || selectedHospital.isEmpty()) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValid24HourTime(fromTime) || !isValid24HourTime(toTime)) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Invalid time format. Use HH:mm in 24-hour format.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isFromTimeBeforeToTime(fromTime, toTime)) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "From time must be before To time", Toast.LENGTH_SHORT).show()
            return
        }

        val isAdded = doctor.addAvailability(docId, selectedDay, fromTime, toTime)

        if (isAdded) {
            Person.VibrationUtil.triggerVibrationshort(this)
            Toast.makeText(this, "Availability added successfully", Toast.LENGTH_SHORT).show()
            loadAvailability()
        } else {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Failed to add availability", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isFromTimeBeforeToTime(fromTime: String, toTime: String): Boolean {
        val fromParts = fromTime.split(":").map { it.toInt() }
        val toParts = toTime.split(":").map { it.toInt() }

        val fromMinutes = fromParts[0] * 60 + fromParts[1]
        val toMinutes = toParts[0] * 60 + toParts[1]

        return fromMinutes < toMinutes
    }


    private fun removeAvailability() {
        Person.VibrationUtil.triggerVibrationshort(this)
        if (selectedItemPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select an item to remove", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedItem = availabilityList[selectedItemPosition]
        val parts = selectedItem.split(" - ")
        val selectedDay = parts[0]
        val fromTime = parts[1].split(" to ")[0]

        val isRemoved = doctor.removeAvailability(docId, selectedDay, fromTime)

        if (isRemoved) {
            Person.VibrationUtil.triggerVibrationshort(this)
            Toast.makeText(this, "Availability removed successfully", Toast.LENGTH_SHORT).show()
            loadAvailability()
        } else {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Failed to remove availability", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValid24HourTime(time: String): Boolean {
        val timePattern = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]")
        return timePattern.matcher(time).matches()
    }

    private fun showTimePickerDialog(editText: EditText) {
        Person.VibrationUtil.triggerVibrationshort(this)
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            editText.setText(formattedTime)
        }, hour, minute, true) // Use true for 24-hour format

        timePickerDialog.show()
    }
}
