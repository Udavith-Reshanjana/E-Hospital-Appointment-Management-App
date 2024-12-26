package com.example.e_hospitalappointmentmanagementapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Patient

class PatientReg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_reg)
    }

    fun patientSignUp(view: View) {
        val patientFirstName = findViewById<EditText>(R.id.PFirstname).text.toString().trim()
        val patientLastName = findViewById<EditText>(R.id.PLastname).text.toString().trim()
        val patientEmail = findViewById<EditText>(R.id.PEmail).text.toString().trim()
        val patientBirthday = findViewById<EditText>(R.id.PBirthday).text.toString().trim()
        val patientPassword = findViewById<EditText>(R.id.Ppass).text.toString()
        val patientPasswordConf = findViewById<EditText>(R.id.Ppassconf).text.toString()
        val genderRadioGroup = findViewById<RadioGroup>(R.id.Pgender)
        val termsCheckBox = findViewById<CheckBox>(R.id.checkBox)

        val patient = Patient(this)

        // Validate input fields
        if (patientFirstName.isEmpty() || patientLastName.isEmpty() || patientEmail.isEmpty() || patientBirthday.isEmpty() || patientPassword.isEmpty() || patientPasswordConf.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(patientEmail).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (patient.isEmailExists(patientEmail)) {
            Toast.makeText(this, "Your email is already exists", Toast.LENGTH_SHORT).show()
            return
        }

        if (!(patient.isValidDate(patientBirthday))) {
            Toast.makeText(this, "Please follow \"YYYY-MM-DD\" format for birthday", Toast.LENGTH_SHORT).show()
            return
        }

        if (patientPassword != patientPasswordConf) {
            Toast.makeText(this, "Passwords and confirmation password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (patientPassword.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGenderId = genderRadioGroup.checkedRadioButtonId
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedGender = findViewById<RadioButton>(selectedGenderId).text.toString()

        if (!termsCheckBox.isChecked) {
            Toast.makeText(this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Save patient data to the database
            val isRegistered = patient.signUpPerson(
                firstName = patientFirstName,
                lastName = patientLastName,
                personEmail = patientEmail,
                personPassword = patientPassword,
                profilePic = null,
                role = 2,
                docSpecialty = null,
                medicalLicence = null,
                birthDay = patientBirthday
            )

            if (isRegistered) {
                Toast.makeText(this, "Patient Registered Successfully!", Toast.LENGTH_LONG).show()
                clearPatientForm()
                gotoAnyScreen(Login::class.java)
            } else {
                Toast.makeText(this, "Patient registration failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error during registration: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to clear the patient registration form
    private fun clearPatientForm() {
        findViewById<EditText>(R.id.PFirstname).text = null
        findViewById<EditText>(R.id.PLastname).text = null
        findViewById<EditText>(R.id.PEmail).text = null
        findViewById<EditText>(R.id.PBirthday).text = null
        findViewById<EditText>(R.id.Ppass).text = null
        findViewById<EditText>(R.id.Ppassconf).text = null
        findViewById<RadioGroup>(R.id.Pgender).clearCheck()
        findViewById<CheckBox>(R.id.checkBox).isChecked = false
    }

    // Function to navigate to any activity
    private fun gotoAnyScreen(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    fun gotoDoctorRegistration(view: View) {
        gotoAnyScreen(Docreg::class.java)
    }

}