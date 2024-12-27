package com.example.e_hospitalappointmentmanagementapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.e_hospitalappointmentmanagementapp.classes.Doctor
import com.example.e_hospitalappointmentmanagementapp.classes.Person

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

    }

    fun logIn(view: View) {
        val userEmail = findViewById<EditText>(R.id.emailSignup)
        val userPassword = findViewById<EditText>(R.id.Passwordsignup)

        val uEmail = userEmail.text.toString().trim()
        val uPword = userPassword.text.toString().trim()

        // Validate input
        if (uEmail.isEmpty() || uPword.isEmpty()) {
            Toast.makeText(this, "Please fill in both email and password!", Toast.LENGTH_SHORT).show()
            return
        }

        // Log in the user
        val person = Person(this)
        val isLoggedIn = person.logUser(uEmail, uPword)

        if (isLoggedIn) {
            val userTypeNumber = person.getPersontype(uEmail, uPword)
            gotoPersonMainScreen(userTypeNumber, uEmail, uPword)
        } else {
            Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun gotoPersonMainScreen(userTypeInt: Int, userEmail: String, userPassword: String) {
        when (userTypeInt) {
            1 -> {
                val doctor = Doctor(this)
                val firstName = doctor.getDoctorFirstName(userEmail, userPassword)
                val bundle = Bundle()
                bundle.putString("first_name", "Dr. $firstName")
                val intent = Intent(this, Doctormain::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
                finish()
            }
            2 -> {
                val intent = Intent(this, Patientmain::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                Toast.makeText(this, "Admin main UI is under development", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun forgotPassword(view: View) {
        Toast.makeText(this, "Forgot password section under development", Toast.LENGTH_SHORT).show()
    }

    fun signUp(view: View) {
        // Create an AlertDialog for user verification
        val options = arrayOf("Doctor", "Patient")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verify your role")
        builder.setItems(options) { _, which ->
            gotoPersonRegistrationScreen(which)
        }

        builder.create().show()
    }

    private fun gotoPersonRegistrationScreen(userTypeInt: Int) {
        when (userTypeInt) {
            0 -> {
                val intent = Intent(this, Docreg::class.java)
                startActivity(intent)
                finish()
            }
            1 -> {
                val intent = Intent(this, PatientReg::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
