package com.kotdev.espress

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class ParcelRecipientDetails : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var phone: EditText
    private lateinit var idNo: EditText
    private lateinit var next: Button
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var user: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_parcel_recipient_details)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        name = findViewById(R.id.et_recipientName)
        phone = findViewById(R.id.et_recipientPhoneNo)
        idNo = findViewById(R.id.et_recipientIDno)
        next = findViewById(R.id.btn_next)
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        next.setOnClickListener {
            //get the input from the user
            val  recipientName = name.text.toString().trim()
            val recipientPhone = phone.text.toString().trim()
            val recipientID = idNo.text.toString().trim()

            val bookedBy: FirebaseUser? = firebaseAuth.currentUser

            if (bookedBy != null) {
                user = bookedBy.email.toString()
            }
            // package the data for database storage
            val parcelDetails2 = hashMapOf(
                "Send by" to user,
                "Recipient Name" to  recipientName,
                "Recipient phone" to recipientPhone,
                "Recipient ID" to recipientID,
                "From" to intent.getStringExtra("From"),
                "To" to intent.getStringExtra("To"),
                "Category" to intent.getStringExtra("Category"),
                "Value" to intent.getStringExtra("Value")
            )
            val id = UUID.randomUUID().toString()
            firebaseDb.collection("Parcel Details")
                .document(id)
                .set(parcelDetails2)
            val bundle = Bundle()
            for ((key,value) in parcelDetails2){
                bundle.putString(key,value)}
            val intent = Intent(this, ParcelCheckoutActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }



    }
}