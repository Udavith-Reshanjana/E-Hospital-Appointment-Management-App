package com.example.e_hospitalappointmentmanagementapp.classes

import android.content.Context
import android.database.sqlite.SQLiteDatabase

class Doctor(context: Context) : Person(context) {

    // Method to retrieve all appointments for a doctor by person_id
    fun getAppointmentsByDoctor(personId: Int): List<Map<String, String>> {
        val appointments = mutableListOf<Map<String, String>>()

        val db = dbHelper.readableDatabase
        val query = """
        SELECT 
            APPOINMENT.APPOINMENT_ID, 
            APPOINMENT.APPOINMENT_DATE, 
            APPOINMENT.STATUS, 
            APPOINMENT.BOOKED_DATE, 
            APPOINMENT.FEEDBACK, 
            PAYMENT.AMOUNT AS PAYMENT_AMOUNT 
        FROM 
            APPOINMENT
        LEFT JOIN 
            PAYMENT ON PAYMENT.PAYMENT_ID = 
            (SELECT PAYMENT_ID FROM NOTIFICATION WHERE NOTIFICATION.APPOINMENT_ID = APPOINMENT.APPOINMENT_ID)
        WHERE 
            APPOINMENT.PERSON_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(personId.toString()))
        try {
            while (cursor.moveToNext()) {
                val appointment = mutableMapOf<String, String>()
                appointment["APPOINMENT_ID"] = cursor.getInt(cursor.getColumnIndexOrThrow("APPOINMENT_ID")).toString()
                appointment["APPOINMENT_DATE"] = cursor.getString(cursor.getColumnIndexOrThrow("APPOINMENT_DATE"))
                appointment["STATUS"] = cursor.getString(cursor.getColumnIndexOrThrow("STATUS"))
                appointment["BOOKED_DATE"] = cursor.getString(cursor.getColumnIndexOrThrow("BOOKED_DATE"))
                appointment["FEEDBACK"] = cursor.getString(cursor.getColumnIndexOrThrow("FEEDBACK"))
                appointment["PAYMENT"] = cursor.getString(cursor.getColumnIndexOrThrow("PAYMENT_AMOUNT")) ?: "0.0"
                appointments.add(appointment)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return appointments
    }



    // Method for remove an appoinment
    fun removeAppointment(appointmentId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val rowsDeleted = db.delete("Appointments", "AppointmentID = ?", arrayOf(appointmentId.toString()))
            db.close()
            rowsDeleted > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
