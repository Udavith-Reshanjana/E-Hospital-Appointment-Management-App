package com.example.e_hospitalappointmentmanagementapp.classes

import android.content.Context

class Doctor(context: Context) : Person(context) {

    fun getDoctorFirstName(userEmail: String, userPassword: String): String? {
        return try {
            dbHelper.readableDatabase.use { db ->
                val query = "SELECT FIRST_NAME FROM PERSON WHERE EMAIL = ? AND PERSON_PASSWORD = ?"
                db.rawQuery(query, arrayOf(userEmail, userPassword)).use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME"))
                    } else {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
