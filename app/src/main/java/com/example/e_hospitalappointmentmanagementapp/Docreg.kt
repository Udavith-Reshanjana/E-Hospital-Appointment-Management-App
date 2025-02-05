package com.example.e_hospitalappointmentmanagementapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor
import com.example.e_hospitalappointmentmanagementapp.classes.Person
import java.io.ByteArrayOutputStream

class Docreg : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docreg)
    }

    private val PICK_IMAGE_REQUEST = 1
    private var medicalLicensePic: ByteArray? = null

    fun docSignUp(view: View) {
        val docFirstName = findViewById<EditText>(R.id.DFirstname).text.toString().trim()
        val docLastName = findViewById<EditText>(R.id.DLastname).text.toString().trim()
        val docEmail = findViewById<EditText>(R.id.DEmail).text.toString().trim()
        val docSpecialty = findViewById<EditText>(R.id.Dspecialty).text.toString().trim()
        val docPassword = findViewById<EditText>(R.id.Dpass).text.toString()
        val docPasswordConf = findViewById<EditText>(R.id.Dpassconf).text.toString()
        val genderRadioGroup = findViewById<RadioGroup>(R.id.Dgender)
        val termsCheckBox = findViewById<CheckBox>(R.id.checkBox)

        val doctor = Doctor(this)

        // Validate input fields
        if (docFirstName.isEmpty() || docLastName.isEmpty() || docEmail.isEmpty() || docSpecialty.isEmpty() || docPassword.isEmpty() || docPasswordConf.isEmpty()) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(docEmail).matches()) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (doctor.isEmailExists(docEmail)) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Given email already exists", Toast.LENGTH_SHORT).show()
            return
        }

        if (docPassword != docPasswordConf) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Passwords and confirm password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (docPassword.length < 8) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGenderId = genderRadioGroup.checkedRadioButtonId
        if (selectedGenderId == -1) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedGender = findViewById<RadioButton>(selectedGenderId).text.toString()

        if (!termsCheckBox.isChecked) {
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show()
            return
        }

        // Use the uploaded profile picture (already converted to a byte array)
        val medicalLicence: ByteArray? = medicalLicensePic

        try {
            // Save doctor data to the database
            val isRegistered = doctor.signUpPerson(
                firstName = docFirstName,
                lastName = docLastName,
                personEmail = docEmail,
                personPassword = docPassword,
                profilePic = null,
                role = 1,
                docSpecialty = docSpecialty,
                medicalLicence = medicalLicence, // Save the uploaded image data or null
                birthDay = null
            )

            if (isRegistered) {
                Person.VibrationUtil.triggerVibrationshort(this)
                Toast.makeText(this, "Doctor Registered Successfully!", Toast.LENGTH_LONG).show()
                clearForm()
                gotoAnyScreen(Login::class.java)
            } else {
                Person.VibrationUtil.triggerVibration(this)
                Toast.makeText(this, "Doctor registration failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Person.VibrationUtil.triggerVibration(this)
            Toast.makeText(this, "Error during registration: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    // function for go to any class
    private fun gotoAnyScreen(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    // Function to clear the registration form
    private fun clearForm() {
        findViewById<EditText>(R.id.DFirstname).text = null
        findViewById<EditText>(R.id.DLastname).text = null
        findViewById<EditText>(R.id.DEmail).text = null
        findViewById<EditText>(R.id.Dspecialty).text = null
        findViewById<EditText>(R.id.Dpass).text = null
        findViewById<EditText>(R.id.Dpassconf).text = null
        findViewById<RadioGroup>(R.id.Dgender).clearCheck()
        findViewById<CheckBox>(R.id.checkBox).isChecked = false
    }

    fun uploadProfilePic(view: View) {
        Person.VibrationUtil.triggerVibrationshort(this)
        // Launch the file picker intent
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle the result of the file picker intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                try {
                    // Convert the image to a Bitmap
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                    // Convert the Bitmap to ByteArray
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    medicalLicensePic = byteArrayOutputStream.toByteArray()
                    Toast.makeText(
                        this,
                        "Medical license uploaded successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Person.VibrationUtil.triggerVibration(this)
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to upload profile picture", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun gotoPatietRegistration(view: View) {
        Person.VibrationUtil.triggerVibrationshort(this)
        gotoAnyScreen(PatientReg::class.java)
    }
}

