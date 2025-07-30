package com.kotdev.espress

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.firestore.FirebaseFirestore

class ParcelDetailsActivity : AppCompatActivity() {


    private lateinit var from: Spinner
    private lateinit var to: Spinner
    private lateinit var category: Spinner
    private lateinit var value: EditText
    private lateinit var next: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_parcel_details)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //initialize the UI
        from = findViewById(R.id.fromSpinner)
        to = findViewById(R.id.toSpinner)
        category = findViewById(R.id.categorySpinner)
        value = findViewById(R.id.aprroxValue)
        next = findViewById(R.id.btn_Next)
        //set the locations for the spinner
        val locations = listOf("Nairobi","Mwingi","Kitui","Machakos","Mombasa")
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            locations
        )
        adapter.setDropDownViewResource(R.layout.spinner_item)
        //set the locations for the spinner
        from.adapter = adapter
        to.adapter = adapter
        //set the options for the parcel categories spinner
        val categories = listOf("Parcel","Luggage")
        val categoryAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            categories
        )
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item)
        category.adapter = categoryAdapter

        next.setOnClickListener {
            //get the user input
            val fromLocation = from.selectedItem.toString()
            val toLocation = to.selectedItem.toString()
            val parcelCategory = category.selectedItem.toString()
            val aproximateValue = value.text.toString().trim()
            // package user input to send to he next activity
            val parcelDetails1 = hashMapOf(
                "From" to fromLocation,
                "To" to toLocation,
                "Category" to parcelCategory,
                "Value" to aproximateValue,
            )
            val bundle = Bundle()
            for ((key,value) in parcelDetails1){
                bundle.putString(key,value)}
            val intent = Intent(this, ParcelRecipientDetails::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
}