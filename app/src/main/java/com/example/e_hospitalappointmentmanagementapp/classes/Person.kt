package com.example.e_hospitalappointmentmanagementapp.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.sql.Blob

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

    // person registration function
    fun signUpPerson(
        firstName: String,
        lastName: String?,
        personEmail: String,
        personPassword: String,
        profilePic: ByteArray?,
        role: Int,
        docSpecialty: String?,
        medicalLicence: ByteArray?,
        age: Int?
    ): Boolean {
        return try {
            dbHelper.writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put("FIRST_NAME", firstName)
                    put("LAST_NAME", lastName)
                    put("EMAIL", personEmail)
                    put("PERSON_PASSWORD", personPassword)
                    put("PROFILE_PIC", profilePic)
                    put("ROLE_TYPE", role)
                    put("DOC_SPECIALITY", docSpecialty)
                    put("MEDICAL_LICENCE", medicalLicence)
                    put("AGE", age)
                }
                val rowId = db.insert("PERSON", null, values)
                rowId != -1L // Returns true if insertion was successful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return !email.matches(emailPattern.toRegex())
    }

    fun isEmailExists(email: String): Boolean {
        return try {
            dbHelper.readableDatabase.use { db ->
                val query = "SELECT 1 FROM PERSON WHERE EMAIL = ?"
                db.rawQuery(query, arrayOf(email)).use { cursor ->
                    cursor.moveToFirst() // Returns true if a record is found
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
