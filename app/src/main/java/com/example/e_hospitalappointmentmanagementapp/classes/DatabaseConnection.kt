package com.example.e_hospitalappointmentmanagementapp.classes

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseConnection(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS ROLES (
                ROLE_ID INTEGER NOT NULL UNIQUE,
                ROLE_NAME TEXT NOT NULL UNIQUE,
                PRIMARY KEY(ROLE_ID)
            );
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS PERSON (
                PERSON_ID INTEGER NOT NULL UNIQUE,
                FIRST_NAME TEXT NOT NULL,
                LAST_NAME TEXT,
                EMAIL TEXT NOT NULL UNIQUE,
                PERSON_PASSWORD TEXT NOT NULL,
                PROFILE_PIC BLOB,
                ROLE_TYPE INTEGER NOT NULL,
                DOC_SPECIALITY TEXT,
                MEDICAL_LICENCE BLOB,
                BIRTHDAY TEXT,
                PRIMARY KEY(PERSON_ID),
                FOREIGN KEY(ROLE_TYPE) REFERENCES ROLES(ROLE_ID)
            );
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS HOSPITAL (
                HOSPITAL_ID INTEGER NOT NULL UNIQUE,
                PERSON_ID INTEGER,
                HOSPITAL_NAME TEXT NOT NULL,
                ADDED_DATE TEXT NOT NULL,
                PRIMARY KEY(HOSPITAL_ID),
                FOREIGN KEY(PERSON_ID) REFERENCES PERSON(PERSON_ID)
            );
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS DOC_AVAILABILITY (
                PERSON_ID INTEGER NOT NULL,
                AVAILABLE_DAY TEXT NOT NULL,
                AVAILABLE_TIME TEXT NOT NULL,
                AVAILABLE_TIME_END TEXT NOT NULL,
                PRIMARY KEY(PERSON_ID, AVAILABLE_DAY, AVAILABLE_TIME, AVAILABLE_TIME_END),
                FOREIGN KEY(PERSON_ID) REFERENCES PERSON(PERSON_ID)
            );
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS PAYMENT (
                PAYMENT_ID INTEGER NOT NULL UNIQUE,
                PAYMENT_DATE TEXT NOT NULL,
                TIME TEXT NOT NULL,
                AMOUNT REAL,
                PRIMARY KEY(PAYMENT_ID)
            );
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS APPOINMENT (
                APPOINMENT_ID INTEGER NOT NULL UNIQUE,
                PERSON_ID INTEGER NOT NULL,
                APPOINMENT_DATE TEXT NOT NULL,
                STATUS TEXT NOT NULL,
                BOOKED_DATE DATE NOT NULL,
                FEEDBACK TEXT,
                PRIMARY KEY(APPOINMENT_ID),
                FOREIGN KEY(PERSON_ID) REFERENCES PERSON(PERSON_ID)
            );
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS NOTIFICATION (
                NOTIFICATION_ID INTEGER NOT NULL UNIQUE,
                PAYMENT_ID INTEGER,
                PERSON_ID INTEGER,
                APPOINMENT_ID INTEGER,
                PRIMARY KEY(NOTIFICATION_ID, PAYMENT_ID, PERSON_ID, APPOINMENT_ID),
                FOREIGN KEY(APPOINMENT_ID) REFERENCES APPOINMENT(APPOINMENT_ID),
                FOREIGN KEY(PAYMENT_ID) REFERENCES PAYMENT(PAYMENT_ID),
                FOREIGN KEY(PERSON_ID) REFERENCES PERSON(PERSON_ID)
            );
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS PATIENT_APPOINMENT (
                PERSON_ID INTEGER NOT NULL,
                APPOINMENT_ID INTEGER NOT NULL,
                PRIMARY KEY(PERSON_ID, APPOINMENT_ID),
                FOREIGN KEY(APPOINMENT_ID) REFERENCES APPOINMENT(APPOINMENT_ID),
                FOREIGN KEY(PERSON_ID) REFERENCES PERSON(PERSON_ID)
            );
        """)

        // Insert data
        db.execSQL("INSERT INTO DOC_AVAILABILITY VALUES (1, 'Monday', '07:00', '12:00');")
        db.execSQL("INSERT INTO DOC_AVAILABILITY VALUES (1, 'Tuesday', '12:00', '15:00');")
        db.execSQL("INSERT INTO HOSPITAL VALUES (1, 1, 'ABC Hospital', '2024-12-24');")
        db.execSQL("INSERT INTO PERSON VALUES (1,'Udavith','Reshanjana','abc@email.com','11111', NULL, 1,'Neurology',NULL, NULL);")
        db.execSQL("INSERT INTO PERSON VALUES (2,'Tharuka','Fernando','def@email.com','11111', NULL, 2,NULL,NULL,'2001-01-01');")
        db.execSQL("INSERT INTO PERSON VALUES (3,'Dave','Jones','blackpearl@pirate.com','11111', NULL,3,NULL,NULL,NULL);")
        db.execSQL("INSERT INTO ROLES VALUES (1, 'Doctor');")
        db.execSQL("INSERT INTO ROLES VALUES (2, 'Patient');")
        db.execSQL("INSERT INTO ROLES VALUES (3, 'Admin');")
//        db.execSQL("INSERT INTO APPOINMENT VALUES (1, 1, '2024-12-31', 'Scheduled', '2024-12-30', 'Looking forward to the appointment.');");
//        db.execSQL("INSERT INTO APPOINMENT VALUES (2, 1, '2025-01-05', 'Completed', '2024-12-28', 'Excellent service.');");
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop existing tables
        db.execSQL("DROP TABLE IF EXISTS NOTIFICATION;")
        db.execSQL("DROP TABLE IF EXISTS PATIENT_APPOINMENT;")
        db.execSQL("DROP TABLE IF EXISTS APPOINMENT;")
        db.execSQL("DROP TABLE IF EXISTS PAYMENT;")
        db.execSQL("DROP TABLE IF EXISTS DOC_AVAILABILITY;")
        db.execSQL("DROP TABLE IF EXISTS HOSPITAL;")
        db.execSQL("DROP TABLE IF EXISTS PERSON;")
        db.execSQL("DROP TABLE IF EXISTS ROLES;")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "HospitalDatabase.db"
        const val DATABASE_VERSION = 1
    }
}
