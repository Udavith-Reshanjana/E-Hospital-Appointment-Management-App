package com.example.e_hospitalappointmentmanagementapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.e_hospitalappointmentmanagementapp.classes.Patient
import com.example.e_hospitalappointmentmanagementapp.classes.Person

class Patientmain : AppCompatActivity() {

    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patientmain)
        openFragment(Home())
        buttoncolor(findViewById(R.id.Homebutton))


        val homebutton = findViewById<ImageButton>(R.id.Homebutton)
        val chatbutton = findViewById<ImageButton>(R.id.Chatbutton)
        val notificationbutton = findViewById<ImageButton>(R.id.Notificationbutton)
        val profilebutton = findViewById<ImageButton>(R.id.Profilebutton)
        val settingsbutton = findViewById<ImageButton>(R.id.Settingsbutton)


        homebutton.setOnClickListener {
            openFragment(Home())
            buttoncolor(homebutton)
            Person.VibrationUtil.triggerVibrationshort(this)
        }

        chatbutton.setOnClickListener {
            openFragment(chatbot())
            buttoncolor(chatbutton)
            Person.VibrationUtil.triggerVibrationshort(this)
        }

        notificationbutton.setOnClickListener {
            openFragment(notification())
            buttoncolor(notificationbutton)
            Person.VibrationUtil.triggerVibrationshort(this)
        }

        profilebutton.setOnClickListener {
            openFragment(patientprofile())
            buttoncolor(profilebutton)
            Person.VibrationUtil.triggerVibrationshort(this)
        }

        settingsbutton.setOnClickListener {
            openFragment(settings())
            buttoncolor(settingsbutton)
            Person.VibrationUtil.triggerVibrationshort(this)
        }

        val firstName = intent.getStringExtra("first_name")
        val personId = intent.getIntExtra("person_id", -1)

        bundle.putString("first_name", firstName)
        bundle.putInt("person_id", personId)
    }

    private fun openFragment(fragment: Fragment) {
        fragment.arguments = bundle
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentviewer, fragment)
        fragmentTransaction.addToBackStack(null) // Add the transaction to the back stack
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

