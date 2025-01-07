package com.example.e_hospitalappointmentmanagementapp.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.widget.Toast
import java.io.ByteArrayOutputStream

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
            // Correct table and column name
            val rowsDeleted = db.delete("APPOINMENT", "APPOINMENT_ID = ?", arrayOf(appointmentId.toString()))
            rowsDeleted > 0 // Returns true if at least one row was deleted
        } catch (e: Exception) {
            e.printStackTrace()
            false // Returns false if an exception occurs
        } finally {
            db.close() // Ensure the database is closed
        }
    }


    fun getAvailabilityForDoctor(personId: Int): List<Map<String, String>> {
        val availability = mutableListOf<Map<String, String>>()
        val db = dbHelper.readableDatabase
        val query = "SELECT AVAILABLE_DAY, AVAILABLE_TIME, AVAILABLE_TIME_END FROM DOC_AVAILABILITY WHERE PERSON_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(personId.toString()))
        while (cursor.moveToNext()) {
            availability.add(
                mapOf(
                    "AVAILABLE_DAY" to cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_DAY")),
                    "AVAILABLE_TIME" to cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME")),
                    "AVAILABLE_TIME_END" to cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME_END"))
                )
            )
        }
        cursor.close()
        db.close()
        return availability
    }

    fun addAvailability(personId: Int, day: String, fromTime: String, toTime: String): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            db.execSQL(
                "INSERT INTO DOC_AVAILABILITY (PERSON_ID, AVAILABLE_DAY, AVAILABLE_TIME, AVAILABLE_TIME_END) VALUES (?, ?, ?, ?)",
                arrayOf(personId, day, fromTime, toTime)
            )
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun removeAvailability(personId: Int, day: String, fromTime: String): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            // Delete only the specified availability based on PERSON_ID, AVAILABLE_DAY, and AVAILABLE_TIME
            val rowsDeleted = db.delete(
                "DOC_AVAILABILITY",
                "PERSON_ID = ? AND AVAILABLE_DAY = ? AND AVAILABLE_TIME = ?",
                arrayOf(personId.toString(), day, fromTime)
            )
            rowsDeleted > 0 // Return true if at least one row was deleted
        } catch (e: Exception) {
            e.printStackTrace()
            false // Return false if any exception occurs
        } finally {
            db.close() // Ensure the database connection is closed
        }
    }

    fun getHospitalsForDoctor(personId: Int): List<String> {
        val hospitals = mutableListOf<String>()
        val db = dbHelper.readableDatabase

        val query = "SELECT HOSPITAL_NAME FROM HOSPITAL WHERE PERSON_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(personId.toString()))

        try {
            while (cursor.moveToNext()) {
                hospitals.add(cursor.getString(cursor.getColumnIndexOrThrow("HOSPITAL_NAME")))
            }
        } finally {
            cursor.close()
            db.close()
        }

        return hospitals
    }

    fun updateProfile(
        personId: Int,
        firstName: String,
        lastName: String,
        oldPassword: String,
        newPassword: String
    ): Boolean {
        val db = dbHelper.writableDatabase

        // Validate old password if new password is provided
        if (newPassword.isNotBlank()) {
            val query = "SELECT PERSON_PASSWORD FROM PERSON WHERE PERSON_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(personId.toString()))
            if (cursor.moveToFirst()) {
                val currentPassword = cursor.getString(cursor.getColumnIndexOrThrow("PERSON_PASSWORD"))
                if (currentPassword != oldPassword) {
                    cursor.close()
                    db.close()
                    Toast.makeText(context, "Old password is incorrect", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            cursor.close()

            if (newPassword == oldPassword) {
                Toast.makeText(context, "New password cannot be the same as the old password", Toast.LENGTH_SHORT).show()
                db.close()
                return false
            }
        }

        // Update profile data
        return try {
            val contentValues = ContentValues().apply {
                put("FIRST_NAME", firstName)
                put("LAST_NAME", lastName)
                if (newPassword.isNotBlank()) put("PERSON_PASSWORD", newPassword)
            }

            val rowsUpdated = db.update(
                "PERSON",
                contentValues,
                "PERSON_ID = ?",
                arrayOf(personId.toString())
            )
            rowsUpdated > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun getProfile(personId: Int): Map<String, Any?>? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT FIRST_NAME, LAST_NAME, EMAIL, PERSON_PASSWORD, PROFILE_PIC 
        FROM PERSON 
        WHERE PERSON_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(personId.toString()))

        return if (cursor.moveToFirst()) {
            mapOf(
                "FIRST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")),
                "LAST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")),
                "EMAIL" to cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")),
                "PERSON_PASSWORD" to cursor.getString(cursor.getColumnIndexOrThrow("PERSON_PASSWORD")),
                "PROFILE_PIC" to cursor.getBlob(cursor.getColumnIndexOrThrow("PROFILE_PIC")) // Fetch profile picture as Blob
            )
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }


    fun getHospitals(personId: Int): List<String> {
        val hospitals = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT HOSPITAL_NAME 
        FROM HOSPITAL 
        WHERE PERSON_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(personId.toString()))

        try {
            while (cursor.moveToNext()) {
                hospitals.add(cursor.getString(cursor.getColumnIndexOrThrow("HOSPITAL_NAME")))
            }
        } finally {
            cursor.close()
            db.close()
        }

        return hospitals
    }

    fun getAppointmentDetails(personId: Int, appointmentId: String): Map<String, String>? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT 
            A.APPOINMENT_ID, 
            A.APPOINMENT_DATE, 
            A.STATUS, 
            A.BOOKED_DATE, 
            A.FEEDBACK, 
            P.AMOUNT AS PAYMENT 
        FROM 
            APPOINMENT A
        LEFT JOIN 
            PAYMENT P ON P.PAYMENT_ID = 
            (SELECT PAYMENT_ID FROM NOTIFICATION WHERE NOTIFICATION.APPOINMENT_ID = A.APPOINMENT_ID)
        WHERE 
            A.PERSON_ID = ? AND A.APPOINMENT_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(personId.toString(), appointmentId))

        return if (cursor.moveToFirst()) {
            val appointmentDetails = mutableMapOf<String, String>()
            appointmentDetails["APPOINMENT_ID"] = cursor.getInt(cursor.getColumnIndexOrThrow("APPOINMENT_ID")).toString()
            appointmentDetails["APPOINMENT_DATE"] = cursor.getString(cursor.getColumnIndexOrThrow("APPOINMENT_DATE"))
            appointmentDetails["STATUS"] = cursor.getString(cursor.getColumnIndexOrThrow("STATUS"))
            appointmentDetails["BOOKED_DATE"] = cursor.getString(cursor.getColumnIndexOrThrow("BOOKED_DATE"))
            appointmentDetails["FEEDBACK"] = cursor.getString(cursor.getColumnIndexOrThrow("FEEDBACK"))
            appointmentDetails["PAYMENT"] = cursor.getString(cursor.getColumnIndexOrThrow("PAYMENT")) ?: "0.0"

            appointmentDetails
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

    fun updateProfileFields(personId: Int, fields: Map<String, String>, oldPassword: String): Boolean {
        return try {
            dbHelper.writableDatabase.use { db ->
                // Validate the old password if a new password is being updated
                if (fields.containsKey("NEW_PASSWORD")) {
                    val cursor = db.rawQuery(
                        "SELECT PERSON_PASSWORD FROM PERSON WHERE PERSON_ID = ?",
                        arrayOf(personId.toString())
                    )
                    if (cursor.moveToFirst()) {
                        val storedPassword = cursor.getString(0)
                        if (storedPassword != oldPassword) {
                            return false // Old password does not match
                        }
                    }
                    cursor.close()
                }

                val values = ContentValues().apply {
                    fields.forEach { (key, value) ->
                        if (key == "NEW_PASSWORD") {
                            put("PERSON_PASSWORD", value)
                        } else {
                            put(key, value)
                        }
                    }
                }
                val rowsAffected = db.update(
                    "PERSON",
                    values,
                    "PERSON_ID = ?",
                    arrayOf(personId.toString())
                )
                rowsAffected > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun updateAppointmentStatus(appointmentId: Int, newStatus: String): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val contentValues = ContentValues().apply {
                put("STATUS", newStatus)
            }
            val rowsUpdated = db.update("APPOINMENT", contentValues, "APPOINMENT_ID = ?", arrayOf(appointmentId.toString()))
            rowsUpdated > 0 // Returns true if at least one row was updated
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }
}
