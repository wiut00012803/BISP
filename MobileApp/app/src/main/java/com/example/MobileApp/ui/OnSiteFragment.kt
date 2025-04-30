package com.example.MobileApp.ui

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MobileApp.R
import com.example.MobileApp.model.*
import com.example.MobileApp.network.RetrofitClient
import com.example.MobileApp.util.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OnSiteFragment : Fragment() {
    private lateinit var etSearch: AutoCompleteTextView
    private lateinit var spinnerCards: Spinner
    private lateinit var etAmount: EditText
    private lateinit var btnPay: Button
    private val suggestions = mutableListOf<Location>()
    private lateinit var suggestionAdapter: ArrayAdapter<String>
    private var selectedLoc: Location? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.fragment_onsite, c, false)

    override fun onViewCreated(v: View, s: Bundle?) {
        etSearch = v.findViewById(R.id.actvLocation)
        spinnerCards = v.findViewById(R.id.spinnerCards)
        etAmount = v.findViewById(R.id.etPayAmount)
        btnPay = v.findViewById(R.id.btnPay)

        suggestionAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            suggestions.map { it.name }.toMutableList()
        )
        etSearch.setAdapter(suggestionAdapter)
        etSearch.threshold = 1

        etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {
                val q = s?.toString() ?: ""
                if (q.length >= 1) {
                    RetrofitClient.apiService.getLocations(Prefs.getToken(requireContext())!!, q)
                        .enqueue(object : Callback<List<Location>> {
                            override fun onResponse(
                                c: Call<List<Location>>, r: Response<List<Location>>
                            ) {
                                if (r.isSuccessful) {
                                    suggestions.clear()
                                    r.body()?.let { sug -> suggestions.addAll(sug) }
                                    suggestionAdapter.clear()
                                    suggestionAdapter.addAll(suggestions.map { it.name })
                                    suggestionAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(c: Call<List<Location>>, t: Throwable) {}
                        })
                }
            }

            override fun beforeTextChanged(a: CharSequence?, b: Int, c: Int, d: Int) {}
            override fun onTextChanged(a: CharSequence?, b: Int, c: Int, d: Int) {}
        })

        etSearch.setOnItemClickListener { parent, view, pos, _ ->
            selectedLoc = suggestions[pos]
            loadCards()
            spinnerCards.visibility = View.VISIBLE
            etAmount.visibility = View.VISIBLE
            btnPay.visibility = View.VISIBLE
        }

        btnPay.setOnClickListener {
            val cardNum = spinnerCards.selectedItem as String
            val locId = selectedLoc?.id ?: return@setOnClickListener
            val amt = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            RetrofitClient.apiService.onSitePay(
                    Prefs.getToken(requireContext())!!,
                    cardNum,
                    locId,
                    amt
                ).enqueue(object : Callback<OnSiteTransaction> {
                    override fun onResponse(
                        c: Call<OnSiteTransaction>, r: Response<OnSiteTransaction>
                    ) {
                        if (r.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Paid. CB=${r.body()?.cashback_amount}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(c: Call<OnSiteTransaction>, t: Throwable) {}
                })
        }
    }

    private fun loadCards() {
        RetrofitClient.apiService.getCards(Prefs.getToken(requireContext())!!)
            .enqueue(object : Callback<List<Card>> {
                override fun onResponse(c: Call<List<Card>>, r: Response<List<Card>>) {
                    if (r.isSuccessful) {
                        val nums = r.body()!!.map { it.card_number }
                        spinnerCards.adapter = ArrayAdapter(
                            requireContext(), android.R.layout.simple_spinner_item, nums
                        )
                    }
                }

                override fun onFailure(c: Call<List<Card>>, t: Throwable) {}
            })
    }
}