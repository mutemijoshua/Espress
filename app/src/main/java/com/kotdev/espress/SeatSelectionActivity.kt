package com.kotdev.espress
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
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
class SeatSelectionActivity : AppCompatActivity() {
    private lateinit var btnNext :Button
    private lateinit var firebaseDb:FirebaseFirestore
    private lateinit var firebaseAuth : FirebaseAuth
    private lateinit var user : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seat_selection)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //instantiate the database and firebase authentication
        firebaseDb = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val selectedSeat = getSelectedSeats()
        //get the current logged in use
        val bookedBy: FirebaseUser? = firebaseAuth.currentUser
        if (bookedBy != null) {
            user = bookedBy.email.toString()
        }
        //read/extract the data passed from the previous activity
        val bookingDetails2 = hashMapOf(
            "Booked by" to user,
            "Date" to intent.getStringExtra("Date"),
            "To" to intent.getStringExtra("To"),
            "From" to intent.getStringExtra("From"),
            "Time" to intent.getStringExtra("Time"),
            "Seat number" to selectedSeat.toString())
        //package the data again and send it to the next activity
        val bundle = Bundle()
        for ((key,value) in bookingDetails2){
            bundle.putString(key,value)}
        //get random id for the database id
        val id = UUID.randomUUID().toString()
        btnNext = findViewById(R.id.btn_Next)
        btnNext.setOnClickListener {
           /* firebaseDb.collection("BookedTrips")
                .document(id)
                .set(bookingDetails)
            */
            val selectedSeat = getSelectedSeats()
            val bookingDetails2 = hashMapOf(
                "Booked by" to user,
                "Date" to intent.getStringExtra("Date"),
                "To" to intent.getStringExtra("To"),
                "From" to intent.getStringExtra("From"),
                "Time" to intent.getStringExtra("Time"),
                "Seat number" to selectedSeat.toString())
            //package the data again and send it to the next activity
            val bundle = Bundle()
            for ((key,value) in bookingDetails2){
                bundle.putString(key,value)}
            val intent = Intent(this,CheckOutActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }
    private fun getSelectedSeats(): List<Int> {
        val selectedSeats = mutableListOf<Int>()
        val seatIds = listOf(
            R.id.seat_1, R.id.seat_2, R.id.seat_3, R.id.seat_4,
            R.id.seat_5, R.id.seat_6, R.id.seat_7, R.id.seat_8,
            R.id.seat_9, R.id.seat_10, R.id.seat_11, R.id.seat_12,
            R.id.seat_13,R.id.seat_14,R.id.seat_15,R.id.seat_16
        )
        for ((index, id) in seatIds.withIndex()) {
            val checkBox = findViewById<CheckBox>(id)
            if (checkBox.isChecked) {
                selectedSeats.add(index + 1) // Seat number starts from 1
            }
        }
        return selectedSeats
    }


}