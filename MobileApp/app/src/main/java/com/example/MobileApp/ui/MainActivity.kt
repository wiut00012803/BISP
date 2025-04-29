package com.example.MobileApp.ui


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MobileApp.R
import com.example.MobileApp.model.*
import com.example.MobileApp.network.RetrofitClient
import com.example.MobileApp.util.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var rvAll: RecyclerView
    private lateinit var rvSource: RecyclerView
    private lateinit var btnContinue: Button
    private lateinit var btnTransfer: Button
    private lateinit var etDest: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnLogout: Button
    private var sourceOptions = listOf<SourceOption>()
    private var selectedSource = ""

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_main)
        rvAll = findViewById(R.id.rvAllCards)
        rvSource = findViewById(R.id.rvSource)
        btnContinue = findViewById(R.id.btnContinue)
        btnTransfer = findViewById(R.id.btnTransfer)
        etDest = findViewById(R.id.etDest)
        etAmount = findViewById(R.id.etAmount)
        btnLogout = findViewById(R.id.btnLogout)
        rvAll.layoutManager = LinearLayoutManager(this)
        rvSource.layoutManager = LinearLayoutManager(this)
        loadAllCards()
        btnContinue.setOnClickListener {
            val dest = etDest.text.toString()
            if (dest.length == 16) {
                RetrofitClient.apiService.getSourceOptions(Prefs.getToken(this)!!, dest)
                    .enqueue(object : Callback<List<SourceOption>> {
                        override fun onResponse(
                            c: Call<List<SourceOption>>, r: Response<List<SourceOption>>
                        ) {
                            if (r.isSuccessful) {
                                sourceOptions = r.body() ?: listOf()
                                rvSource.adapter = SourceAdapter(sourceOptions) {
                                    selectedSource = it
                                    Toast.makeText(
                                        this@MainActivity, "Selected $it", Toast.LENGTH_SHORT
                                    ).show()
                                }
                                rvSource.visibility = RecyclerView.VISIBLE
                                etAmount.visibility = EditText.VISIBLE
                                btnTransfer.visibility = Button.VISIBLE
                            }
                        }

                        override fun onFailure(c: Call<List<SourceOption>>, t: Throwable) {}
                    })
            }
        }
        btnTransfer.setOnClickListener {
            val dest = etDest.text.toString()
            val amt = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            RetrofitClient.apiService.transfer(
                    Prefs.getToken(this)!!, TransferRequest(selectedSource, dest, amt)
                ).enqueue(object : Callback<TransferResponse> {
                    override fun onResponse(
                        c: Call<TransferResponse>, r: Response<TransferResponse>
                    ) {
                        if (r.isSuccessful) {
                            Toast.makeText(
                                this@MainActivity, "Transferred", Toast.LENGTH_LONG
                            ).show()
                            loadAllCards()
                        } else {
                            Toast.makeText(
                                this@MainActivity, "Fail", Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(c: Call<TransferResponse>, t: Throwable) {}
                })
        }
        btnLogout.setOnClickListener {
            Prefs.clear(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadAllCards() {
        RetrofitClient.apiService.getCards(Prefs.getToken(this)!!)
            .enqueue(object : Callback<List<Card>> {
                override fun onResponse(
                    c: Call<List<Card>>, r: Response<List<Card>>
                ) {
                    if (r.isSuccessful) {
                        rvAll.adapter = CardAdapter(r.body()!!)
                    }
                }

                override fun onFailure(c: Call<List<Card>>, t: Throwable) {}
            })
    }
}