package com.example.yourapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        val nextButton = findViewById<Button>(R.id.nextButton)

        val button1 = findViewById<ImageButton>(R.id.button1)
        val button2 = findViewById<ImageButton>(R.id.button2)
        val button3 = findViewById<ImageButton>(R.id.button3)
        val button4 = findViewById<ImageButton>(R.id.button4)

        // Set click listeners to navigate to other pages
        nextButton.setOnClickListener {
            // Navigate to the next screen logic here
        }

        button1.setOnClickListener {
            // Navigate to another page
        }

        button2.setOnClickListener {
            // Navigate to another page
        }

        button3.setOnClickListener {
            // Navigate to another page
        }

        button4.setOnClickListener {
            // Navigate to another page
        }
    }
}