package com.example.e_hospitalappointmentmanagementapp

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.e_hospitalappointmentmanagementapp.classes.Patient
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class patientprofile : Fragment() {

    private var personId: Int = -1
    private var personName: String = ""
    private lateinit var patient: Patient
    private lateinit var firstName: TextView
    private lateinit var birthdayField: EditText
    private lateinit var oldPasswordField: EditText
    private lateinit var newPasswordField: EditText
    private lateinit var toggleOldPasswordButton: ImageButton
    private lateinit var toggleNewPasswordButton: ImageButton

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val resizedBitmap = resizeBitmap(bitmap, 400, 400)

                val byteArrayOutputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val profilePicByteArray = byteArrayOutputStream.toByteArray()

                val success = patient.changePersonProfilePicture(personId, profilePicByteArray)

                if (success) {
                    val profileImageButton = requireView().findViewById<ImageButton>(R.id.propic)
                    profileImageButton.setImageBitmap(resizedBitmap)
                    Toast.makeText(context, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_patientprofile, container, false)

        // Retrieve data from arguments
        arguments?.let {
            personId = it.getInt("person_id", -1)
            personName = it.getString("first_name", "Unknown")
        }

        if (personId == -1) {
            Toast.makeText(context, "Invalid person ID", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return null
        }

        patient = Patient(requireContext())

        // Initialize UI elements
        firstName = view.findViewById(R.id.textView11)
        birthdayField = view.findViewById(R.id.profilebirthday)
        oldPasswordField = view.findViewById(R.id.profilepw)
        newPasswordField = view.findViewById(R.id.profilepwc)
        toggleOldPasswordButton = view.findViewById(R.id.toggleOldPassword)
        toggleNewPasswordButton = view.findViewById(R.id.toggleNewPassword)

        // Set listeners
        birthdayField.setOnClickListener { showDatePicker() }
        view.findViewById<Button>(R.id.savebutton).setOnClickListener { updateProfile() }
        view.findViewById<Button>(R.id.logoutbutton).setOnClickListener { logout() }
        view.findViewById<ImageButton>(R.id.propic).setOnClickListener { changeProfilePictureOfPatient() }

        // Toggle password visibility
        toggleOldPasswordButton.setOnClickListener { togglePasswordVisibility(oldPasswordField, toggleOldPasswordButton) }
        toggleNewPasswordButton.setOnClickListener { togglePasswordVisibility(newPasswordField, toggleNewPasswordButton) }

        // Set the first name in the header
        firstName.text = personName

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load profile data after the view is fully created
        loadProfileData()
    }

    private fun loadProfileData() {
        // Retrieve the patient profile data
        val patientProfile = patient.getProfile(personId)

        if (patientProfile != null) {
            val firstNameText = patientProfile["FIRST_NAME"] as String?
            val lastNameText = patientProfile["LAST_NAME"] as String?
            val birthdayText = patientProfile["BIRTHDAY"] as String?
            val profilePic = patientProfile["PROFILE_PIC"] as ByteArray?

            // Access views using requireView()
            val rootView = requireView()

            val firstNameField = rootView.findViewById<EditText>(R.id.patientprofilefirstname)
            val lastNameField = rootView.findViewById<EditText>(R.id.patientprofilelastname)
            val birthdayField = rootView.findViewById<EditText>(R.id.profilebirthday)
            val profileImageButton = rootView.findViewById<ImageButton>(R.id.propic)

            firstNameField?.setText(firstNameText ?: "")
            lastNameField?.setText(lastNameText ?: "")
            birthdayField?.setText(birthdayText ?: "")

            if (profilePic != null && profilePic.isNotEmpty()) {
                val resizedBitmap = resizeBitmap(
                    BitmapFactory.decodeByteArray(profilePic, 0, profilePic.size),
                    400,
                    400
                )
                profileImageButton?.setImageBitmap(resizedBitmap)
                profileImageButton?.contentDescription = "Profile Image"
            } else {
                profileImageButton?.setImageDrawable(null)
                profileImageButton?.setBackgroundResource(android.R.color.darker_gray)
                profileImageButton?.contentDescription = "No Image Available"
            }

            Toast.makeText(context, "Profile loaded successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to load profile data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun updateProfile1() {
        val firstName = view?.findViewById<EditText>(R.id.patientprofilefirstname)?.text.toString()
        val lastName = view?.findViewById<EditText>(R.id.patientprofilelastname)?.text.toString()
        val birthday = birthdayField.text.toString()
        val oldPassword = oldPasswordField.text.toString()
        val newPassword = newPasswordField.text.toString()

        if (firstName.isBlank() || lastName.isBlank() || birthday.isBlank()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate birthday
        if (!isValidDate(birthday)) {
            Toast.makeText(context, "Invalid birthday. Choose a past date.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.isNotBlank() && newPassword == oldPassword) {
            Toast.makeText(context, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show()
            return
        }

        val success = patient.updateProfile(personId, firstName, lastName, null, oldPassword, newPassword, birthday)

        if (success) {
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile() {
        val firstName = view?.findViewById<EditText>(R.id.patientprofilefirstname)?.text.toString()
        val lastName = view?.findViewById<EditText>(R.id.patientprofilelastname)?.text.toString()
        val birthday = birthdayField.text.toString()
        val oldPassword = oldPasswordField.text.toString()
        val newPassword = newPasswordField.text.toString()

        // Validate mandatory fields
        if (firstName.isBlank() || lastName.isBlank() || birthday.isBlank()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate birthday
        if (!isValidDate(birthday)) {
            Toast.makeText(context, "Invalid birthday. Choose a past date.", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if new password is the same as the old password
        if (newPassword.isNotBlank() && newPassword == oldPassword) {
            Toast.makeText(context, "New password cannot be the same as the old password.", Toast.LENGTH_SHORT).show()
            return
        }

        // Attempt to update profile in the database
        val success = patient.updateProfile(personId, firstName, lastName, null, oldPassword, newPassword, birthday)

        if (success) {
            // Navigate to Home fragment with updated details
            val homeFragment = Home()
            homeFragment.arguments = Bundle().apply {
                putInt("person_id", personId)
                putString("first_name", firstName)
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentviewer, homeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()

            Toast.makeText(context, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
        } else {
            // Specific error handling
            if (!patient.isCorrectPassword(personId, oldPassword)) {
                Toast.makeText(context, "Incorrect old password. Please try again.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to update profile. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showDatePicker1() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                if (selectedDate.after(Calendar.getInstance())) {
                    Toast.makeText(context, "Birthday cannot be today or a future date.", Toast.LENGTH_SHORT).show()
                } else {
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    birthdayField.setText(format.format(selectedDate.time))
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentDate = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                if (selectedDate.after(currentDate)) {
                    Toast.makeText(context, "Birthday cannot be today or a future date.", Toast.LENGTH_SHORT).show()
                } else {
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    birthdayField.setText(format.format(selectedDate.time))
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Restrict the max date to yesterday to ensure birthday cannot be today or a future date
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 86400000 // Yesterday's date
        datePickerDialog.show()
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
        editText.setSelection(editText.text.length)
    }

    private fun logout() {
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

    private fun isValidDate(dateString: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.isLenient = false
            val date = dateFormat.parse(dateString) ?: return false
            !date.after(Date())
        } catch (e: Exception) {
            false
        }
    }

    private fun changeProfilePictureOfPatient() {
        pickImageLauncher.launch("image/*")
    }
}
