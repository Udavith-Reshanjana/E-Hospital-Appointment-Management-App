package com.example.e_hospitalappointmentmanagementapp.classes

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


}