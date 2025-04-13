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

class SignupActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fullName: EditText
    private lateinit var email: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var password1: EditText
    private lateinit var password2:EditText
    private lateinit var signUpButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        fullName = findViewById(R.id.fullname)
        email = findViewById(R.id.email)
        phoneNumber = findViewById(R.id.phonenumber)
        password1 = findViewById(R.id.password1)
        password2 = findViewById(R.id.password2)
        signUpButton = findViewById(R.id.signupButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener{
            val Email = email.toString()
            val phone = phoneNumber.toString()
            val name = fullName.toString()
            val pass1 = password1.toString()
            val pass2 = password2.toString()

            if (Email.isNotEmpty() && pass1.isNotEmpty() && pass2.isNotEmpty()&& phone.isNotEmpty()&& name.isNotEmpty()){
                if (pass1 == pass2){
                    firebaseAuth.createUserWithEmailAndPassword(Email,pass1).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this,"Account Created Successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this ,LoginActivity::class.java)
                            startActivity(intent)
                        }else{
                            Toast.makeText(this, "An Error occurred please try again eater", Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this,"Password is not matching", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Please fill in all the fields",Toast.LENGTH_SHORT).show()
            }
        }
    }
}