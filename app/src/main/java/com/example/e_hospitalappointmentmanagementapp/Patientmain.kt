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

        val homebutton = findViewById<ImageButton>(R.id.Homebutton)

        homebutton.setOnClickListener {
            openFragment(Home()) // Ensure `Home` extends `androidx.fragment.app.Fragment`
        }
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentviewer, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: Add the transaction to the back stack
        fragmentTransaction.commit()
    }
}
