package com.example.e_hospitalappointmentmanagementapp.classes

import android.content.ContentValues
import android.content.Context

class Patient(context: Context) : Person(context) {

    fun getAllDoctors(): List<Map<String, String>> {
        val doctors = mutableListOf<Map<String, String>>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT 
            PERSON.PERSON_ID, 
            PERSON.FIRST_NAME, 
            PERSON.LAST_NAME, 
            PERSON.EMAIL, 
            PERSON.DOC_SPECIALITY
        FROM 
            PERSON
        WHERE 
            PERSON.ROLE_TYPE = 1
    """
        val cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val doctor = mapOf(
                    "PERSON_ID" to cursor.getInt(cursor.getColumnIndexOrThrow("PERSON_ID")).toString(),
                    "FIRST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")),
                    "LAST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")),
                    "EMAIL" to cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")),
                    "DOC_SPECIALITY" to cursor.getString(cursor.getColumnIndexOrThrow("DOC_SPECIALITY"))
                )
                doctors.add(doctor)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return doctors
    }


    fun searchDoctors(query: String): List<Map<String, String>> {
        val doctors = mutableListOf<Map<String, String>>()
        val db = dbHelper.readableDatabase
        val wildcardQuery = "%$query%"
        val searchQuery = """
        SELECT 
            PERSON.PERSON_ID, 
            PERSON.FIRST_NAME, 
            PERSON.LAST_NAME, 
            PERSON.EMAIL, 
            PERSON.DOC_SPECIALITY, 
            DOC_AVAILABILITY.AVAILABLE_DAY, 
            DOC_AVAILABILITY.AVAILABLE_TIME, 
            DOC_AVAILABILITY.AVAILABLE_TIME_END
        FROM 
            PERSON
        LEFT JOIN 
            DOC_AVAILABILITY 
        ON 
            PERSON.PERSON_ID = DOC_AVAILABILITY.PERSON_ID
        WHERE 
            (PERSON.FIRST_NAME LIKE ? OR PERSON.LAST_NAME LIKE ? OR (PERSON.FIRST_NAME || ' ' || PERSON.LAST_NAME) LIKE ?)
            AND PERSON.ROLE_TYPE = 1
    """
        val cursor = db.rawQuery(searchQuery, arrayOf(wildcardQuery, wildcardQuery, wildcardQuery))

        try {
            while (cursor.moveToNext()) {
                val doctor = mapOf(
                    "PERSON_ID" to cursor.getInt(cursor.getColumnIndexOrThrow("PERSON_ID")).toString(),
                    "FIRST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")),
                    "LAST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")),
                    "EMAIL" to cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")),
                    "DOC_SPECIALITY" to cursor.getString(cursor.getColumnIndexOrThrow("DOC_SPECIALITY")),
                    "AVAILABILITY" to "${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_DAY"))}, " +
                            "${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME"))}-${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME_END"))}"
                )
                doctors.add(doctor)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return doctors
    }

    fun filterDoctorsByTime(fromTime: String, toTime: String): List<Map<String, String>> {
        val doctors = mutableListOf<Map<String, String>>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT 
            PERSON.PERSON_ID, 
            PERSON.FIRST_NAME, 
            PERSON.LAST_NAME, 
            PERSON.EMAIL, 
            PERSON.DOC_SPECIALITY, 
            DOC_AVAILABILITY.AVAILABLE_DAY, 
            DOC_AVAILABILITY.AVAILABLE_TIME, 
            DOC_AVAILABILITY.AVAILABLE_TIME_END
        FROM 
            PERSON
        LEFT JOIN 
            DOC_AVAILABILITY 
        ON 
            PERSON.PERSON_ID = DOC_AVAILABILITY.PERSON_ID
        WHERE 
            DOC_AVAILABILITY.AVAILABLE_TIME >= ? 
            AND DOC_AVAILABILITY.AVAILABLE_TIME_END <= ?
            AND PERSON.ROLE_TYPE = 1
    """
        val cursor = db.rawQuery(query, arrayOf(fromTime, toTime))

        try {
            while (cursor.moveToNext()) {
                val doctor = mapOf(
                    "PERSON_ID" to cursor.getInt(cursor.getColumnIndexOrThrow("PERSON_ID")).toString(),
                    "FIRST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")),
                    "LAST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")),
                    "EMAIL" to cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")),
                    "DOC_SPECIALITY" to cursor.getString(cursor.getColumnIndexOrThrow("DOC_SPECIALITY")),
                    "AVAILABILITY" to "${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_DAY"))}, " +
                            "${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME"))}-${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME_END"))}"
                )
                doctors.add(doctor)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return doctors
    }

    fun getDoctorIdByName(doctorName: String): Int {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT PERSON_ID 
        FROM PERSON 
        WHERE ROLE_TYPE = 1 AND (FIRST_NAME || ' ' || LAST_NAME) = ?
    """
        val cursor = db.rawQuery(query, arrayOf(doctorName))

        return try {
            if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow("PERSON_ID"))
            } else {
                -1 // Return -1 if no doctor is found
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun filterDoctorsBySpecializationAndHospital(
        specialization: String,
        hospital: String
    ): List<Map<String, String>> {
        val doctors = mutableListOf<Map<String, String>>()
        val db = dbHelper.readableDatabase

        // Build query dynamically based on filters
        val queryBuilder = StringBuilder(
            """
        SELECT 
            PERSON.PERSON_ID, 
            PERSON.FIRST_NAME, 
            PERSON.LAST_NAME, 
            PERSON.EMAIL, 
            PERSON.DOC_SPECIALITY, 
            DOC_AVAILABILITY.AVAILABLE_DAY, 
            DOC_AVAILABILITY.AVAILABLE_TIME, 
            DOC_AVAILABILITY.AVAILABLE_TIME_END, 
            HOSPITAL.HOSPITAL_NAME
        FROM 
            PERSON
        LEFT JOIN 
            DOC_AVAILABILITY 
        ON 
            PERSON.PERSON_ID = DOC_AVAILABILITY.PERSON_ID
        LEFT JOIN 
            HOSPITAL 
        ON 
            PERSON.PERSON_ID = HOSPITAL.PERSON_ID
        WHERE 
            PERSON.ROLE_TYPE = 1
    """
        )

        val args = mutableListOf<String>()

        if (specialization != "Select Specialization") {
            queryBuilder.append(" AND PERSON.DOC_SPECIALITY = ?")
            args.add(specialization)
        }

        if (hospital != "Select Hospital") {
            queryBuilder.append(" AND HOSPITAL.HOSPITAL_NAME = ?")
            args.add(hospital)
        }

        val query = queryBuilder.toString()
        val cursor = db.rawQuery(query, args.toTypedArray())

        try {
            while (cursor.moveToNext()) {
                val doctor = mapOf(
                    "PERSON_ID" to cursor.getInt(cursor.getColumnIndexOrThrow("PERSON_ID")).toString(),
                    "FIRST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")),
                    "LAST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")),
                    "EMAIL" to cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")),
                    "DOC_SPECIALITY" to cursor.getString(cursor.getColumnIndexOrThrow("DOC_SPECIALITY")),
                    "AVAILABILITY" to "${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_DAY"))}, " +
                            "${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME"))}-${cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME_END"))}",
                    "HOSPITALS" to cursor.getString(cursor.getColumnIndexOrThrow("HOSPITAL_NAME"))
                )
                doctors.add(doctor)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return doctors
    }

    fun getAllSpecializations(): List<String> {
        val specializations = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT DISTINCT DOC_SPECIALITY 
        FROM PERSON 
        WHERE ROLE_TYPE = 1 AND DOC_SPECIALITY IS NOT NULL
    """
        val cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val specialization = cursor.getString(cursor.getColumnIndexOrThrow("DOC_SPECIALITY"))
                specializations.add(specialization)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return specializations
    }

    fun getAllHospitals(): List<String> {
        val hospitals = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT DISTINCT HOSPITAL_NAME 
        FROM HOSPITAL
    """
        val cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val hospitalName = cursor.getString(cursor.getColumnIndexOrThrow("HOSPITAL_NAME"))
                hospitals.add(hospitalName)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return hospitals
    }

    fun getAppointmentsByPersonId(personId: Int): List<Map<String, String>> {
        val appointments = mutableListOf<Map<String, String>>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT 
            APPOINMENT.APPOINMENT_ID,
            APPOINMENT.APPOINMENT_DATE,
            APPOINMENT.STATUS,
            APPOINMENT.BOOKED_DATE,
            APPOINMENT.FEEDBACK
        FROM 
            PATIENT_APPOINMENT
        INNER JOIN 
            APPOINMENT 
        ON 
            PATIENT_APPOINMENT.APPOINMENT_ID = APPOINMENT.APPOINMENT_ID
        WHERE 
            PATIENT_APPOINMENT.PERSON_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(personId.toString()))

        try {
            while (cursor.moveToNext()) {
                val appointment = mapOf(
                    "APPOINMENT_ID" to cursor.getInt(cursor.getColumnIndexOrThrow("APPOINMENT_ID")).toString(),
                    "APPOINMENT_DATE" to cursor.getString(cursor.getColumnIndexOrThrow("APPOINMENT_DATE")),
                    "STATUS" to cursor.getString(cursor.getColumnIndexOrThrow("STATUS")),
                    "BOOKED_DATE" to cursor.getString(cursor.getColumnIndexOrThrow("BOOKED_DATE")),
                    "FEEDBACK" to (cursor.getString(cursor.getColumnIndexOrThrow("FEEDBACK")) ?: "No feedback")
                )
                appointments.add(appointment)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return appointments
    }

    fun getHospitalsByDoctorId(doctorId: Int): List<String> {
        val hospitals = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT DISTINCT HOSPITAL.HOSPITAL_NAME 
        FROM HOSPITAL
        WHERE HOSPITAL.PERSON_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(doctorId.toString()))

        try {
            while (cursor.moveToNext()) {
                val hospitalName = cursor.getString(cursor.getColumnIndexOrThrow("HOSPITAL_NAME"))
                hospitals.add(hospitalName)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return hospitals
    }


    fun getDoctorById(doctorId: Int): Map<String, Any?>? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT 
            PERSON.PERSON_ID, 
            PERSON.FIRST_NAME, 
            PERSON.LAST_NAME, 
            PERSON.EMAIL, 
            PERSON.DOC_SPECIALITY, 
            PERSON.PROFILE_PIC
        FROM 
            PERSON
        WHERE 
            PERSON.PERSON_ID = ? AND PERSON.ROLE_TYPE = 1
    """
        val cursor = db.rawQuery(query, arrayOf(doctorId.toString()))

        return try {
            if (cursor.moveToFirst()) {
                mapOf(
                    "PERSON_ID" to cursor.getInt(cursor.getColumnIndexOrThrow("PERSON_ID")).toString(),
                    "FIRST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")),
                    "LAST_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")),
                    "EMAIL" to cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")),
                    "DOC_SPECIALITY" to cursor.getString(cursor.getColumnIndexOrThrow("DOC_SPECIALITY")),
                    "PROFILE_PIC" to cursor.getBlob(cursor.getColumnIndexOrThrow("PROFILE_PIC"))
                )
            } else {
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun getDoctorAvailableDays(doctorId: Int): List<String> {
        val availableDays = mutableListOf<String>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT DISTINCT AVAILABLE_DAY 
        FROM DOC_AVAILABILITY
        WHERE PERSON_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(doctorId.toString()))

        try {
            while (cursor.moveToNext()) {
                val day = cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_DAY"))
                availableDays.add(day)
            }
        } finally {
            cursor.close()
            db.close()
        }

        return availableDays
    }

    fun insertAppointment(
        doctorId: Int,
        appointmentDate: String,
        bookedDate: String,
        status: String,
        feedback: String
    ): Int {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("PERSON_ID", doctorId)
            put("APPOINMENT_DATE", appointmentDate)
            put("STATUS", status)
            put("BOOKED_DATE", bookedDate)
            put("FEEDBACK", feedback)
        }
        val appointmentId = db.insert("APPOINMENT", null, contentValues).toInt()
        db.close()
        return appointmentId
    }

    fun insertPatientAppointment(patientId: Int, appointmentId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val contentValues = ContentValues().apply {
                put("PERSON_ID", patientId)
                put("APPOINMENT_ID", appointmentId)
            }
            db.insert("PATIENT_APPOINMENT", null, contentValues) != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }


    fun insertPayment(paymentDate: String, paymentTime: String, amount: Double): Int {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("PAYMENT_DATE", paymentDate)
            put("TIME", paymentTime)
            put("AMOUNT", amount)
        }
        val paymentId = db.insert("PAYMENT", null, contentValues).toInt()
        db.close()
        return paymentId
    }

    fun getHospitalNameByAppointment(appointmentId: Int): String? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT HOSPITAL.HOSPITAL_NAME
        FROM HOSPITAL
        INNER JOIN APPOINMENT ON HOSPITAL.PERSON_ID = APPOINMENT.PERSON_ID
        WHERE APPOINMENT.APPOINMENT_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(appointmentId.toString()))
        return try {
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow("HOSPITAL_NAME"))
            } else {
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun getDoctorAvailableTimePeriod(doctorId: Int): String? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT AVAILABLE_TIME, AVAILABLE_TIME_END
        FROM DOC_AVAILABILITY
        WHERE PERSON_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(doctorId.toString()))
        return try {
            if (cursor.moveToFirst()) {
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME"))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow("AVAILABLE_TIME_END"))
                "$startTime - $endTime"
            } else {
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun getAppointmentDetails(appointmentId: Int): Map<String, String>? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT APPOINMENT.APPOINMENT_DATE, APPOINMENT.BOOKED_DATE, APPOINMENT.STATUS, APPOINMENT.FEEDBACK,
               PAYMENT.AMOUNT AS PAYMENT, PERSON.FIRST_NAME || ' ' || PERSON.LAST_NAME AS DOCTOR_NAME
        FROM APPOINMENT
        LEFT JOIN PAYMENT ON APPOINMENT.APPOINMENT_ID = PAYMENT.PAYMENT_ID
        LEFT JOIN PERSON ON APPOINMENT.PERSON_ID = PERSON.PERSON_ID
        WHERE APPOINMENT.APPOINMENT_ID = ?
    """
        val cursor = db.rawQuery(query, arrayOf(appointmentId.toString()))

        return try {
            if (cursor.moveToFirst()) {
                mapOf(
                    "APPOINTMENT_DATE" to cursor.getString(cursor.getColumnIndexOrThrow("APPOINMENT_DATE")),
                    "BOOKED_DATE" to cursor.getString(cursor.getColumnIndexOrThrow("BOOKED_DATE")),
                    "STATUS" to cursor.getString(cursor.getColumnIndexOrThrow("STATUS")),
                    "FEEDBACK" to (cursor.getString(cursor.getColumnIndexOrThrow("FEEDBACK")) ?: ""),
                    "PAYMENT" to (cursor.getString(cursor.getColumnIndexOrThrow("PAYMENT")) ?: "N/A"),
                    "DOCTOR_NAME" to cursor.getString(cursor.getColumnIndexOrThrow("DOCTOR_NAME"))
                )
            } else {
                null
            }
        } finally {
            cursor.close()
            db.close()
        }
    }

    fun updateAppointmentFeedback(appointmentId: Int, feedback: String): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val contentValues = ContentValues().apply {
                put("FEEDBACK", feedback)
            }
            val rowsUpdated = db.update("APPOINMENT", contentValues, "APPOINMENT_ID = ?", arrayOf(appointmentId.toString()))
            rowsUpdated > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    fun updateProfile(
        personId: Int,
        firstName: String,
        lastName: String?,
        email: String?,
        oldPassword: String,
        newPassword: String?,
        birthday: String?
    ): Boolean {
        return try {
            dbHelper.writableDatabase.use { db ->
                // Verify old password
                val query = "SELECT PERSON_PASSWORD FROM PERSON WHERE PERSON_ID = ?"
                val cursor = db.rawQuery(query, arrayOf(personId.toString()))
                if (cursor.moveToFirst()) {
                    val currentPassword = cursor.getString(cursor.getColumnIndexOrThrow("PERSON_PASSWORD"))
                    cursor.close()

                    if (currentPassword != oldPassword) {
                        return false // Old password doesn't match
                    }
                } else {
                    cursor.close()
                    return false // Person not found
                }

                // Update profile fields
                val contentValues = ContentValues().apply {
                    put("FIRST_NAME", firstName)
                    put("LAST_NAME", lastName)
                    put("BIRTHDAY", birthday)
                    if (email != null) put("EMAIL", email)
                    if (!newPassword.isNullOrBlank()) put("PERSON_PASSWORD", newPassword)
                }

                val rowsUpdated = db.update("PERSON", contentValues, "PERSON_ID = ?", arrayOf(personId.toString()))
                rowsUpdated > 0 // Return true if at least one row was updated
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getProfile(personId: Int): Map<String, Any?>? {
        val db = dbHelper.readableDatabase
        val query = """
        SELECT FIRST_NAME, LAST_NAME, EMAIL, PERSON_PASSWORD, PROFILE_PIC, BIRTHDAY
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
                "PROFILE_PIC" to cursor.getBlob(cursor.getColumnIndexOrThrow("PROFILE_PIC")),
                "BIRTHDAY" to cursor.getString(cursor.getColumnIndexOrThrow("BIRTHDAY"))
            )
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

    fun isCorrectPassword(personId: Int, oldPassword: String): Boolean {
        return try {
            dbHelper.readableDatabase.use { db ->
                val query = "SELECT PERSON_PASSWORD FROM PERSON WHERE PERSON_ID = ?"
                db.rawQuery(query, arrayOf(personId.toString())).use { cursor ->
                    if (cursor.moveToFirst()) {
                        val storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("PERSON_PASSWORD"))
                        storedPassword == oldPassword
                    } else {
                        false // Person not found
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



}