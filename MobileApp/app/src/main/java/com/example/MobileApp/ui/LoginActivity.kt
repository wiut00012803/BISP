package com.example.MobileApp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.MobileApp.R
import com.example.MobileApp.model.LoginResponse
import com.example.MobileApp.network.RetrofitClient
import com.example.MobileApp.util.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        tvRegister.setOnClickListener {
            Log.d("LOGIN", "Login success, token=${Prefs.getToken(this)}")
            startActivity(Intent(this, MainActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            RetrofitClient.apiService
                .login(email, pass)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val token = response.body()?.token ?: ""
                            Prefs.saveToken(this@LoginActivity, "Token $token")
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login failed: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}