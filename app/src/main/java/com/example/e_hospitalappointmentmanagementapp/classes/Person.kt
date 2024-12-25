package com.example.e_hospitalappointmentmanagementapp.classes

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper

open class Person(private val context: Context) {
    protected val dbHelper: SQLiteOpenHelper = DatabaseConnection(context)

    // Log the user in
    fun logUser(userEmail: String, userPassword: String): Boolean {
        return try {
            dbHelper.readableDatabase.use { db ->
                val query = "SELECT * FROM PERSON WHERE EMAIL = ? AND PERSON_PASSWORD = ?"
                db.rawQuery(query, arrayOf(userEmail, userPassword)).use { cursor ->
                    if (cursor.moveToFirst()) {
                        // Login successful
                        val userName = cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME"))
                        // You could store user details in shared preferences if needed
                        true
                    } else {
                        // Invalid credentials
                        false
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // provide person type as doctor, patient ,or admin
    fun getPersontype(userEmail: String, userPassword: String): Int {
        return try {
            dbHelper.readableDatabase.use { db ->
                val query = "SELECT ROLE_TYPE FROM PERSON WHERE EMAIL = ? AND PERSON_PASSWORD = ?"
                db.rawQuery(query, arrayOf(userEmail, userPassword)).use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getInt(cursor.getColumnIndexOrThrow("ROLE_TYPE"))
                    } else {
                        -1 // Return -1 if no user is found
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -2 // Return -2 in case of an error
        }
    }
}
