package com.kotdev.espress

import android.content.ContentValues.TAG
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var userName: TextView
    private lateinit var userEmail: TextView
    private lateinit var userPhoneNumber: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestoreDb: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        userPhoneNumber = findViewById(R.id.userPhonenumber)
        firestoreDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid.toString()

        if (userId != null) {
            firestoreDb.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("Username") ?: ""
                        val email = document.getString("Email") ?: ""
                        val phone = document.getString("Phone") ?: ""

                        // You can display this in the UI
                        userName.text = name
                        userEmail.text = email
                        userPhoneNumber.text = phone

                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
        }
    }
}