package com.example.MobileApp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
    private lateinit var rvHistory: RecyclerView
    private lateinit var rvSource: RecyclerView
    private lateinit var rvOnSite: RecyclerView
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
        rvHistory = findViewById(R.id.rvHistory)
        rvSource = findViewById(R.id.rvSource)
        rvOnSite = findViewById(R.id.rvOnSiteHistory)

        btnContinue = findViewById(R.id.btnContinue)
        btnTransfer = findViewById(R.id.btnTransfer)
        etDest = findViewById(R.id.etDest)
        etAmount = findViewById(R.id.etAmount)
        btnLogout = findViewById(R.id.btnLogout)

        rvAll.layoutManager = LinearLayoutManager(this)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvSource.layoutManager = LinearLayoutManager(this)
        rvOnSite.layoutManager = LinearLayoutManager(this)

        loadHistory()
        loadAllCards()
        loadOnSiteHistory()

        btnContinue.setOnClickListener {
            val dest = etDest.text.toString()
            if (dest.length == 16) {
                RetrofitClient.apiService.getSourceOptions(Prefs.getToken(this)!!, dest)
                    .enqueue(object : Callback<List<SourceOption>> {
                        override fun onResponse(
                            call: Call<List<SourceOption>>, resp: Response<List<SourceOption>>
                        ) {
                            if (resp.isSuccessful) {
                                sourceOptions = resp.body() ?: emptyList()
                                rvSource.adapter = SourceAdapter(sourceOptions) { clicked ->
                                    selectedSource = clicked
                                    Toast.makeText(
                                        this@MainActivity, "Selected $clicked", Toast.LENGTH_SHORT
                                    ).show()
                                }
                                rvSource.visibility = View.VISIBLE

                                etAmount.apply {
                                    visibility = View.VISIBLE
                                    isEnabled = true
                                    isFocusable = true
                                    isFocusableInTouchMode = true
                                    requestFocus()
                                }
                                val imm = getSystemService(
                                    Context.INPUT_METHOD_SERVICE
                                ) as InputMethodManager
                                imm.showSoftInput(etAmount, InputMethodManager.SHOW_IMPLICIT)

                                btnTransfer.visibility = View.VISIBLE
                            }
                        }

                        override fun onFailure(
                            c: Call<List<SourceOption>>, t: Throwable
                        ) {
                        }
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

        val btnOnSite = findViewById<Button>(R.id.btnOnSite)
        btnOnSite.setOnClickListener {
            startActivity(Intent(this, OnSitePaymentActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadOnSiteHistory()
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

    private fun loadHistory() {
        RetrofitClient.apiService.getTransactions(Prefs.getToken(this)!!)
            .enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(
                    call: Call<List<Transaction>>, resp: Response<List<Transaction>>
                ) {
                    if (resp.isSuccessful) {
                        rvHistory.adapter = TransactionAdapter(resp.body()!!)
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {}
            })
    }

    private fun loadOnSiteHistory() {
        RetrofitClient.apiService.getOnSiteHistory(Prefs.getToken(this)!!)
            .enqueue(object : Callback<List<OnSiteTransaction>> {
                override fun onResponse(
                    c: Call<List<OnSiteTransaction>>, r: Response<List<OnSiteTransaction>>
                ) {
                    if (r.isSuccessful) {
                        rvOnSite.adapter = OnSiteTransactionAdapter(r.body()!!)
                    }
                }

                override fun onFailure(c: Call<List<OnSiteTransaction>>, t: Throwable) {}
            })
    }
}