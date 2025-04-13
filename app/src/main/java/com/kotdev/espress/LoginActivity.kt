package com.kotdev.espress

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var Email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton:Button
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        Email = findViewById(R.id.email)
        password =findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)

        firebaseAuth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loginButton.setOnClickListener{
            val email = Email.toString()
            val pass = password.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(this,EspressHomeActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,"Email or Password does not match.please Try again",Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this, "Pllease fill in all the fields",Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null){
            val intent = Intent(this, EspressHomeActivity::class.java)
            startActivity(intent)
        }
    }
}