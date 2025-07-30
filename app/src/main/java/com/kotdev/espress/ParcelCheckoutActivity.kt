package com.kotdev.espress

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ParcelCheckoutActivity : AppCompatActivity() {
    private lateinit var phoneNumber: EditText
    private lateinit var confirmPayment : Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_parcel_checkout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        phoneNumber = findViewById(R.id.phonenumber)
        confirmPayment = findViewById(R.id.btn_confirmpayment)
        //instanciate database and firestore authentication
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()
        //get data from the previous activity
        val parcelDetails3 = hashMapOf(
            "Send by" to intent.getStringExtra("Send by"),
            "Recipient Name" to intent.getStringExtra("Recipient Name"),
            "Recipient phone" to intent.getStringExtra("Recipient phone"),
            "Recipient ID" to intent.getStringExtra("Recipient ID"),
            "From" to intent.getStringExtra("From"),
            "To" to intent.getStringExtra("To"),
            "Category" to intent.getStringExtra("Category"),
            "Value" to intent.getStringExtra("Value")
        )
        //package the data again and send it to the next activity
        val bundle = Bundle()
        for ((key,value) in parcelDetails3){
            bundle.putString(key,value)}
        //check out button listener(Initiates the payment/records data in the database/also passes data to the receipt activity
        confirmPayment.setOnClickListener {
            // val phone = phoneNumber.text.toString()
            val intent = Intent(this,ParcelReceiptActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
}