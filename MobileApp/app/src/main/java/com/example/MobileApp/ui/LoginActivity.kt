package com.example.MobileApp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.MobileApp.R
import com.example.MobileApp.network.RetrofitClient
import com.example.MobileApp.util.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_login)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvReg = findViewById<TextView>(R.id.tvRegister)
        tvReg.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            RetrofitClient.apiService
                .login(email,pass)
                .enqueue(object: Callback<com.example.MobileApp.model.LoginResponse> {
                    override fun onResponse(
                        c: Call<com.example.MobileApp.model.LoginResponse>,
                        r: Response<com.example.MobileApp.model.LoginResponse>
                    ) {
                        if(r.isSuccessful){
                            val token = r.body()?.token ?: ""
                            Prefs.saveToken(this@LoginActivity,"Token $token")
                            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity,
                                "Login failed",Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure(c: Call<com.example.MobileApp.model.LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity,
                            "Error",Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}