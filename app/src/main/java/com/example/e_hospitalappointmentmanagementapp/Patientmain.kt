package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class Patientmain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patientmain)
        openFragment(Appointment())

        val homebutton = findViewById<ImageButton>(R.id.Homebutton)
        val chatbutton = findViewById<ImageButton>(R.id.Chatbutton)
        val notificationbutton = findViewById<ImageButton>(R.id.Notificationbutton)
        val profilebutton = findViewById<ImageButton>(R.id.Profilebutton)
        val settingsbutton = findViewById<ImageButton>(R.id.Settingsbutton)



        homebutton.setOnClickListener {
            openFragment(Home()) // Ensure `Home` extends `androidx.fragment.app.Fragment`
        }

        chatbutton.setOnClickListener {
            openFragment(chatbot()) // Ensure `Home` extends `androidx.fragment.app.Fragment`
        }

        notificationbutton.setOnClickListener {
            openFragment(notification())
        }

        profilebutton.setOnClickListener {
            openFragment(patientprofile())
        }

        settingsbutton.setOnClickListener {
            openFragment(settings())
        }

    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentviewer, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: Add the transaction to the back stack
        fragmentTransaction.commit()
    }
}
