package com.kotdev.espress

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TimePicker
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

//this is the first activity for the book trip functionality
class BookTrip : AppCompatActivity() {
    private lateinit var bookNow: Button
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var from: Spinner
    private lateinit var to: Spinner
    private lateinit var time: Spinner
    private lateinit var date:DatePicker
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_trip)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //initialize firebase authentication and firestore database
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        //initialize the ui(xml)
        bookNow = findViewById(R.id.btn_bookNow)
        from = findViewById(R.id.fromSpinner)
        to = findViewById(R.id.toSpinner)
        time = findViewById(R.id.timeSpinner)
        date = findViewById(R.id.datePicker)
        //list of locations that the user is allowed to select
        val locations = listOf("Nairobi","Mwingi", "Kitui","Machakos","Mombasa")
        //Create an ArrayAdapter with the location list
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            locations
        )
        // 4. Set the dropdown style for the spinner
        adapter.setDropDownViewResource(R.layout.spinner_item)
        // 5. Attach the adapter to the spinner
        from.adapter = adapter
        to.adapter = adapter
        //set the time spinner options
        val timeSlots = listOf("6Am-9Am","9Am-12Pm","12-3Pm","3Pm-6Pm","6Pm-9Pm")
        //create an array adapter for the time spinner
        val timeAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            timeSlots
        )
        // 4. Set the dropdown style for the spinner
        timeAdapter.setDropDownViewResource(R.layout.spinner_item)
        // 5. Attach the adapter to the spinner
        time.adapter = timeAdapter
        //open the next activity and pass the data
        bookNow.setOnClickListener {
            //get the selected locations both to and from
            val fromLocation = from.selectedItem.toString()
            val toLocation = to.selectedItem.toString()
            //get the selected time slot for travel
            val selectedTime = time.selectedItem.toString()
            //retrieve the selected date
            val day = date.dayOfMonth.toString()
            val month = date.month.toString()
            val year = date.year.toString()
            val fullDate = "$day/$month/$year"
            val id = UUID.randomUUID().toString()
            val bookedBy: FirebaseUser? = firebaseAuth.currentUser
            //check if the useris logged in
            if (bookedBy != null) {
                user = bookedBy.email.toString()
            }
            //store the selected travell details in a bundle
            val bookingDetails1= hashMapOf(
                "Booked by" to user,
                "Date" to fullDate,
                "To" to toLocation,
                "From" to fromLocation,
                "Time" to selectedTime
            )
            //record the data and pass it on to the next activity
            val bundle = Bundle()
            for ((key,value) in bookingDetails1){
                bundle.putString(key,value)}
            val intent = Intent(this,SeatSelectionActivity::class.java)
            //send the data to the next activity
            intent.putExtras(bundle)
            //launch the next activity
            startActivity(intent)
        }
    }
}