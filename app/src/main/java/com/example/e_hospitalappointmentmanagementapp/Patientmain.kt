package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class Patientmain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patientmain)
        openFragment(Home())

        val homebutton = findViewById<ImageButton>(R.id.Homebutton)
        val chatbutton = findViewById<ImageButton>(R.id.Chatbutton)
        val notificationbutton = findViewById<ImageButton>(R.id.Notificationbutton)
        val profilebutton = findViewById<ImageButton>(R.id.Profilebutton)
        val settingsbutton = findViewById<ImageButton>(R.id.Settingsbutton)


        homebutton.setOnClickListener {
            openFragment(Home())
            buttoncolor(homebutton)
        }

        chatbutton.setOnClickListener {
            openFragment(chatbot()) // Ensure `Home` extends `androidx.fragment.app.Fragment`
            buttoncolor(chatbutton)
        }

        notificationbutton.setOnClickListener {
            openFragment(notification())
            buttoncolor(notificationbutton)
        }

        profilebutton.setOnClickListener {
            openFragment(patientprofile())
            buttoncolor(profilebutton)
        }

        settingsbutton.setOnClickListener {
            openFragment(settings())
            buttoncolor(settingsbutton)
        }

    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentviewer, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: Add the transaction to the back stack
        fragmentTransaction.commit()
    }

    fun buttoncolor(button: ImageButton){
        findViewById<ImageButton>(R.id.Homebutton).setImageResource(R.drawable.homeicon)
        findViewById<ImageButton>(R.id.Settingsbutton).setImageResource(R.drawable.settingsicon)
        findViewById<ImageButton>(R.id.Chatbutton).setImageResource(R.drawable.chatboticon)
        findViewById<ImageButton>(R.id.Profilebutton).setImageResource(R.drawable.profileicon)
        findViewById<ImageButton>(R.id.Notificationbutton).setImageResource(R.drawable.notificationicon)
        if(button.tag == "Home"){
            button.setImageResource(R.drawable.homeiconclicked)
        } else if(button.tag == "Settings"){
            button.setImageResource(R.drawable.settingsiconclicked)
        } else if(button.tag == "Notifications"){
            button.setImageResource(R.drawable.notificationiconclicked)
        } else if(button.tag == "Chatbot"){
            button.setImageResource(R.drawable.chatboticonclicked)
        } else if(button.tag == "Profile"){
            button.setImageResource(R.drawable.profileiconclicked)
        }
    }

}

