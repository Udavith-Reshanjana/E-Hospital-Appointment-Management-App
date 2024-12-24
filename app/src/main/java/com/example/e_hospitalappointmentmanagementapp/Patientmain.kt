package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton

class Patientmain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patientmain)

        val homebutton = findViewById<ImageButton>(R.id.Homebutton)

        homebutton.setOnClickListener {
            openFragment(Home())
        }
    }

    private fun openFragment(fragment: android.support.v4.app.Fragment) {
        val fragmnetTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmnetTransaction.replace(R.id.fragmentviewer, fragment)
        fragmnetTransaction.addToBackStack(null) // Optional: Add the transaction to the back stack
        fragmnetTransaction.commit()
    }
}