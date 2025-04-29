package com.example.MobileApp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.MobileApp.R
import com.example.MobileApp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirm)
        val btnReg = findViewById<Button>(R.id.btnRegister)

        btnReg.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            val conf = etConfirm.text.toString()
            RetrofitClient.apiService.register(email, pass, conf).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Registered. Please log in.",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(
                                this@RegisterActivity, LoginActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity, "Registration failed", Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@RegisterActivity, "Network error", Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}