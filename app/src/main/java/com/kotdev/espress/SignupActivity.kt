package com.kotdev.espress

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestoreDb: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val fullName = findViewById<EditText>(R.id.fullname)
        val email = findViewById<EditText>(R.id.email)
        val phoneNumber = findViewById<EditText>(R.id.phonenumber)
        val password1 = findViewById<EditText>(R.id.password1)
        val password2 = findViewById<EditText>(R.id.password2)
        val signUpButton = findViewById<Button>(R.id.signupButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        firestoreDb = FirebaseFirestore.getInstance()

        signUpButton.setOnClickListener{
            val userEmail = email.text.toString().trim()
            val phone = phoneNumber.text.toString().trim()
            val name = fullName.text.toString().trim()
            val pass1 = password1.text.toString().trim()
            val pass2 = password2.text.toString().trim()
            if(userEmail.isEmpty()||phone.isEmpty()||name.isEmpty()||pass1.isEmpty()||pass2.isEmpty()){
                Toast.makeText(this,"Please enter your email",Toast.LENGTH_SHORT).show()
            } else if(pass1 != pass2){
                Toast.makeText(this,"Password is not matching", Toast.LENGTH_SHORT).show()
            }else{
                firebaseAuth.createUserWithEmailAndPassword(userEmail,pass1).addOnCompleteListener(this) {
                        if (it.isSuccessful){
                            Toast.makeText(this,"Account Created Successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this ,LoginActivity::class.java)
                            startActivity(intent)
                            val user = firebaseAuth.currentUser
                            val uid = firebaseAuth.currentUser?.uid.toString()

                            if (user != null) {
                                val userData = hashMapOf(
                                    "Email" to userEmail,
                                    "Username" to name,
                                    "Phone" to phone,
                                    "CreatedAt" to System.currentTimeMillis()
                                )

                                firestoreDb.collection("Users")
                                    .document(uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "User successfully added to Firestore")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d("Firestore", "Error writing user to Firestore")
                                    }
                            } else {
                            Log.d("Auth", "User registration failed")
                                                                            }

                        }else{
                            Toast.makeText(this, "An Error occurred please try again eater", Toast.LENGTH_SHORT).show()
                        }
                }

            }

        }
    }
}