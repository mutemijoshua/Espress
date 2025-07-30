package com.kotdev.espress

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Base64
import android.util.Log

import com.kotdev.espress.model.STKPushRequest
import com.kotdev.espress.model.STKPushResponse
import com.kotdev.espress.network.ApiClient
import com.kotdev.espress.network.DarajaApi

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckOutActivity : AppCompatActivity() {
    private lateinit var phoneNumber: EditText
    private lateinit var confirmPayment : Button
    private lateinit var retrofit: Retrofit
    private val consumerKey = "IuuxVpBpflCxxQMEA0SVz8mMxZLAGrA0Fd2FFvOUoEAPDi5y"
    private val consumerSecret = "JZSVE0AGB7cdU4C361mCuGz5AG64IJU35wOtZqurbAvyM8fNbcYgSoAj2YG73TXo"
    private val shortCode = "174379"
    private val passkey = "you_pass_key"
    private val callbackUrl = "url"
    private lateinit var responseText:TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check_out)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        phoneNumber = findViewById(R.id.phonenumber)
        confirmPayment = findViewById(R.id.btn_confirmpayment)
        responseText = findViewById(R.id.textViewResponse)
        retrofit = Retrofit.Builder()
            .baseUrl("https:sandbox.safaricom.co.ke/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //instanciate database and firestore authentication
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()
        //get data from the seat selection activity
        val bookingDetails3 = hashMapOf(
            "Booked by" to intent.getStringExtra("user"),
            "Date" to intent.getStringExtra("Date"),
            "To" to intent.getStringExtra("To"),
            "From" to intent.getStringExtra("From"),
            "Time" to intent.getStringExtra("Time"),
            "Seat number" to intent.getStringExtra("Seat number")
        )
        //daraja code
        val shortCode = "174379"
        val passKey = "YOUR_PASS_KEY"
        val timestamp = generateTimestamp()
        val password = generatePassword(shortCode, passKey, timestamp)
        val token = "Bearer YOUR_ACCESS_TOKEN_FROM_BACKEND"

        val request = STKPushRequest(
            BusinessShortCode = shortCode,
            Password = password,
            Timestamp = timestamp,
            TransactionType = "CustomerPayBillOnline",
            Amount = "1",
            PartyA = "2547XXXXXXXX",
            PartyB = shortCode,
            PhoneNumber = "2547XXXXXXXX",
            CallBackURL = "https://your-backend.com/callback",
            AccountReference = "BUSBOOKING",
            TransactionDesc = "Bus Ticket"
        )

        val darajaApi = ApiClient.retrofit.create(DarajaApi::class.java)
        darajaApi.pushSTK(token, request).enqueue(object : retrofit2.Callback<STKPushResponse> {
            override fun onResponse(call: Call<STKPushResponse>, response: retrofit2.Response<STKPushResponse>) {
                if (response.isSuccessful) {
                    Log.d("M-PESA", "Success: ${response.body()?.CustomerMessage}")
                } else {
                    Log.e("M-PESA", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<STKPushResponse>, t: Throwable) {
                Log.e("M-PESA", "Failure: ${t.localizedMessage}")
            }
        })

        //package the data again and send it to the next activity
        val bundle = Bundle()
        for ((key,value) in bookingDetails3){
            bundle.putString(key,value)}
        //check out button listener(Initiates the payment/records data in the database/also passes data to the receipt activity
        confirmPayment.setOnClickListener {
           // val phone = phoneNumber.text.toString()
            val intent = Intent(this,BusReceiptActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            /*if(phone.isNotEmpty()){
                val intent = Intent(this,ReceiptActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Please enter your phone number",Toast.LENGTH_SHORT).show()
            }
            */
        }
    }
    fun generateTimestamp(): String {
        val dateFormat = java.text.SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date())
    }

    fun generatePassword(shortCode: String, passKey: String, timestamp: String): String {
        val dataToEncode = shortCode + passKey + timestamp
        return android.util.Base64.encodeToString(dataToEncode.toByteArray(), android.util.Base64.NO_WRAP)
    }

}



