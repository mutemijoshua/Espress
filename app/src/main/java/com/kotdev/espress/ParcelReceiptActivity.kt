package com.kotdev.espress

import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class ParcelReceiptActivity : AppCompatActivity() {
    private lateinit var passengerPhone: TextView
    private lateinit var passengerName: TextView
    private lateinit var passengerEmail: TextView
    private  lateinit var bookingDate: TextView
    private lateinit var departureTime: TextView
    private lateinit var destination: TextView
    private lateinit var from: TextView
    private lateinit var numberPlate: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseFirestore
    private lateinit var btnDownloadReceipt: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_parcel_receipt)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        passengerPhone = findViewById(R.id.tvPassengerPhone)
        passengerName = findViewById(R.id.tvPassengerName)
        passengerEmail = findViewById(R.id.tvPassengerEmail)
        bookingDate = findViewById(R.id.tvBookingDate)
        departureTime = findViewById(R.id.tvDeparture)
        destination = findViewById(R.id.tvDestination)
        from = findViewById(R.id.tvFrom)
        numberPlate = findViewById(R.id.tvBusNumber)
        btnDownloadReceipt = findViewById(R.id.btnDownloadReceipt)
        //instanciate Firebase and firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()
        val userId = firebaseAuth.currentUser?.uid.toString()

        if (userId != null) {
            firebaseDb.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("Username") ?: ""
                        val email = document.getString("Email") ?: ""
                        val phone = document.getString("Phone") ?: ""
                        val date = intent.getStringExtra("Date") ?: "N/A"
                        val departure = intent.getStringExtra("Time") ?: "N/A"
                        val location = intent.getStringExtra("From")?: "N/A"
                        val tdestination = intent.getStringExtra("To") ?: "N/A"

                        // You can display this in the UI
                        passengerName.text = "Name: $name"
                        passengerEmail.text = "Email: $email"
                        passengerPhone.text = "Phone: $phone"
                        bookingDate.text = "Date: $date"
                        departureTime.text= "DTime: $departure"
                        destination.text = "Destination: $tdestination"
                        from.text = "From: $location"
                        numberPlate.text = "001"
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "get failed with ", exception)
                }
        }
        btnDownloadReceipt.setOnClickListener {
            val layout = findViewById<View>(R.id.receiptLayout)
            saveReceiptAsPdfScoped(layout)
        }
        btnDownloadReceipt.setOnClickListener {
            val receiptView = findViewById<View>(R.id.receiptLayout)
            val file = saveReceiptAsPdfScoped(receiptView)
            file?.let {
                GmailSender.sendEmailWithAttachment(
                    senderEmail = "joshuamutua555@gmail.com",        // 👈 Use your actual email
                    senderPassword = "azkk pkfj qdlp ovap",          // 👈 Use App Password for Gmail
                    recipientEmail = "joshu.herman97@gmail.com",
                    subject = "Your Bus Booking Receipt",
                    body = "Attached is your receipt for the bus booking.",
                    attachment = it
                ) { success, message ->
                    runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun saveReceiptAsPdfScoped(view: View): File? {
        val fileName = "BusReceipt_${System.currentTimeMillis()}.pdf"
        val directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        if (directory == null) {
            Toast.makeText(this, "Storage not available", Toast.LENGTH_SHORT).show()
            return null
        }

        val pdfFile = File(directory, fileName)

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
        val page = document.startPage(pageInfo)

        view.draw(page.canvas)
        document.finishPage(page)

        try {
            pdfFile.outputStream().use {
                document.writeTo(it)
            }
            Toast.makeText(this, "Saved to ${pdfFile.absolutePath}", Toast.LENGTH_SHORT).show()
            return pdfFile
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            document.close()
        }

        return null
    }
}