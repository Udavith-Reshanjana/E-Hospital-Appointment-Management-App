package com.example.e_hospitalappointmentmanagementapp

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor
import java.io.ByteArrayOutputStream
import java.io.InputStream

class docprofile : AppCompatActivity() {

    private var personId: Int = -1
    private lateinit var doctor: Doctor
    private lateinit var oldPasswordField: EditText
    private lateinit var newPasswordField: EditText
    private lateinit var toggleOldPasswordButton: ImageButton
    private lateinit var toggleNewPasswordButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_docprofile)

        personId = intent.getIntExtra("person_id", -1)
        val firstName = intent.getStringExtra("firstName")

        findViewById<TextView>(R.id.docUsername).text = firstName

        if (personId == -1) {
            Toast.makeText(this, "Invalid person ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        doctor = Doctor(this)

        // Initialize password fields and toggle buttons
        oldPasswordField = findViewById(R.id.docprofilepw)
        newPasswordField = findViewById(R.id.docprofilepwc)
        toggleOldPasswordButton = findViewById(R.id.toggleOldPassword)
        toggleNewPasswordButton = findViewById(R.id.toggleNewPassword)

        // Set toggle password visibility
        toggleOldPasswordButton.setOnClickListener { togglePasswordVisibility(oldPasswordField, toggleOldPasswordButton) }
        toggleNewPasswordButton.setOnClickListener { togglePasswordVisibility(newPasswordField, toggleNewPasswordButton) }

        loadProfileData()
    }

    fun logout(view: View) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun updateProfile(view: View) {
        val fName = findViewById<EditText>(R.id.docprofilefirstname).text.toString()
        val lName = findViewById<EditText>(R.id.docprofilelastname).text.toString()
        val email = findViewById<EditText>(R.id.docprofileemail).text.toString()
        val oldPassword = oldPasswordField.text.toString()
        val newPassword = newPasswordField.text.toString()

        if (fName.isBlank() || lName.isBlank() || email.isBlank()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isNotBlank()) {
            // Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return
            }

            // Validate email uniqueness
            if (doctor.isEmailExists(email)) {
                Toast.makeText(this, "Email already exists. Please use a different email.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (newPassword.isNotBlank() && newPassword == oldPassword) {
            Toast.makeText(this, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show()
            return
        }

        val success = doctor.updateProfile(
            personId,
            fName,
            lName,
            email,
            oldPassword,
            newPassword
        )

        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProfileData() {
        // Retrieve the doctor profile data
        val doctorProfile = doctor.getProfile(personId)

        doctorProfile?.let { profile ->
            // Set the first name, last name, and email fields
            findViewById<EditText>(R.id.docprofilefirstname).setText(profile["FIRST_NAME"] as String? ?: "")
            findViewById<EditText>(R.id.docprofilelastname).setText(profile["LAST_NAME"] as String? ?: "")
            findViewById<EditText>(R.id.docprofileemail).setText(profile["EMAIL"] as String? ?: "")

            // Handle the profile picture
            val profilePic = profile["PROFILE_PIC"] as ByteArray?
            val profileImageButton = findViewById<ImageButton>(R.id.propic)

            if (profilePic != null && profilePic.isNotEmpty()) {
                // Resize and set the profile picture
                val resizedBitmap = resizeBitmap(BitmapFactory.decodeByteArray(profilePic, 0, profilePic.size), 400, 400)
                profileImageButton.setImageBitmap(resizedBitmap)
                profileImageButton.contentDescription = "Profile Image"
            } else {
                // Show a placeholder if no profile picture exists
                profileImageButton.setImageDrawable(null)
                profileImageButton.setBackgroundResource(android.R.color.darker_gray)
                profileImageButton.contentDescription = "No Image Available"
            }

            // Populate the hospital list spinner
            val hospitalList = doctor.getHospitals(personId)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hospitalList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            findViewById<Spinner>(R.id.profilehosptiallist).adapter = adapter
        } ?: run {
            // Handle the case where no profile data is found
            Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            bitmap?.let { bmp ->
                // Resize the image to fit 150dp x 150dp
                val resizedBitmap = resizeBitmap(bmp, 400, 400)

                // Convert the resized bitmap to a byte array
                val byteArray = bitmapToByteArray(resizedBitmap)

                // Update the profile picture in the database
                val success = doctor.changePersonProfilePicture(personId, byteArray)
                if (success) {
                    // Update the UI with the new profile picture
                    findViewById<ImageButton>(R.id.propic).apply {
                        setImageBitmap(resizedBitmap)
                        contentDescription = null
                    }
                    Toast.makeText(this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun changeProfilePicture(view: View) {
        pickImage.launch("image/*")
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageButton) {
        val isPasswordVisible = editText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        if (isPasswordVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            button.setImageResource(R.drawable.ic_eye_closed)
        } else {
            editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            button.setImageResource(R.drawable.ic_eye)
        }
        editText.setSelection(editText.text.length) // Move cursor to the end
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
