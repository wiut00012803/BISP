package com.example.MobileApp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.MobileApp.R
import com.example.MobileApp.model.*
import com.example.MobileApp.network.RetrofitClient
import com.example.MobileApp.util.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnSitePaymentActivity : AppCompatActivity() {
    private lateinit var actvLocation: AutoCompleteTextView
    private lateinit var spinnerCards: Spinner
    private lateinit var etPayAmount: EditText
    private lateinit var btnPay: Button

    private val suggestions = mutableListOf<Location>()
    private lateinit var suggestionAdapter: ArrayAdapter<String>
    private var selectedLocation: Location? = null

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_on_site_payment)

        actvLocation = findViewById(R.id.actvLocation)
        spinnerCards = findViewById(R.id.spinnerCards)
        etPayAmount = findViewById(R.id.etPayAmount)
        btnPay = findViewById(R.id.btnPay)

        suggestionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            suggestions.map { it.name }.toMutableList()
        )
        actvLocation.setAdapter(suggestionAdapter)

        actvLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s?.toString() ?: ""
                if (q.length >= 1) {
                    RetrofitClient.apiService.getLocations(
                            Prefs.getToken(this@OnSitePaymentActivity)!!,
                            q
                        ).enqueue(object : Callback<List<Location>> {
                            override fun onResponse(
                                call: Call<List<Location>>, resp: Response<List<Location>>
                            ) {
                                if (resp.isSuccessful) {
                                    suggestions.clear()
                                    resp.body()?.let { suggestions.addAll(it) }
                                    suggestionAdapter.clear()
                                    suggestionAdapter.addAll(suggestions.map { it.name })
                                    suggestionAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(call: Call<List<Location>>, t: Throwable) {}
                        })
                }
            }
        })

        actvLocation.setOnItemClickListener { parent, view, pos, id ->
            selectedLocation = suggestions[pos]
            loadAndSortCardsByCashback()
            spinnerCards.visibility = View.VISIBLE
            etPayAmount.visibility = View.VISIBLE
            btnPay.visibility = View.VISIBLE
        }

        btnPay.setOnClickListener {
            val cardNum = spinnerCards.selectedItem as String
            val locId = selectedLocation?.id ?: return@setOnClickListener
            val amt = etPayAmount.text.toString().toDoubleOrNull() ?: 0.0
            RetrofitClient.apiService.onSitePay(Prefs.getToken(this)!!, cardNum, locId, amt)
                .enqueue(object : Callback<OnSiteTransaction> {
                    override fun onResponse(
                        call: Call<OnSiteTransaction>, r: Response<OnSiteTransaction>
                    ) {
                        if (r.isSuccessful) {
                            Toast.makeText(
                                this@OnSitePaymentActivity,
                                "Paid. CB=${r.body()?.cashback_amount}",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@OnSitePaymentActivity, "Payment failed", Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<OnSiteTransaction>, t: Throwable) {}
                })
        }
    }

    private fun loadAndSortCardsByCashback() {
        RetrofitClient.apiService.getCards(Prefs.getToken(this)!!)
            .enqueue(object : Callback<List<Card>> {
                override fun onResponse(
                    call: Call<List<Card>>, resp: Response<List<Card>>
                ) {
                    if (resp.isSuccessful) {
                        val sorted = resp.body()!!.sortedByDescending { it.cashback }
                        val numbers = sorted.map { it.card_number }
                        spinnerCards.adapter = ArrayAdapter(
                            this@OnSitePaymentActivity,
                            android.R.layout.simple_spinner_item,
                            numbers
                        ).apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Card>>, t: Throwable) {}
            })
    }
}