package com.example.MobileApp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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

class MainFragment : Fragment() {

    private lateinit var rvAllCards: RecyclerView
    private lateinit var rvSource: RecyclerView
    private lateinit var etDest: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnContinue: Button
    private lateinit var btnTransfer: Button

    private var sourceOptions = listOf<SourceOption>()
    private var selectedSource = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvAllCards = view.findViewById(R.id.rvAllCards)
        rvSource   = view.findViewById(R.id.rvSource)
        etDest     = view.findViewById(R.id.etDest)
        etAmount   = view.findViewById(R.id.etAmount)
        btnContinue= view.findViewById(R.id.btnContinue)
        btnTransfer= view.findViewById(R.id.btnTransfer)

        rvAllCards.layoutManager = LinearLayoutManager(requireContext())
        rvSource.layoutManager   = LinearLayoutManager(requireContext())

        loadAllCards()

        btnContinue.setOnClickListener {
            val dest = etDest.text.toString()
            if (dest.length == 16) {
                RetrofitClient.apiService
                    .getSourceOptions("Token ${Prefs.getToken(requireContext())}", dest)
                    .enqueue(object : Callback<List<SourceOption>> {
                        override fun onResponse(
                            call: Call<List<SourceOption>>,
                            resp: Response<List<SourceOption>>
                        ) {
                            if (resp.isSuccessful) {
                                sourceOptions = resp.body() ?: emptyList()
                                rvSource.adapter = SourceAdapter(sourceOptions) { clicked ->
                                    selectedSource = clicked
                                    Toast.makeText(
                                        requireContext(),
                                        "Selected $clicked",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                rvSource.visibility    = View.VISIBLE
                                etAmount.visibility    = View.VISIBLE
                                btnTransfer.visibility = View.VISIBLE
                            }
                        }
                        override fun onFailure(c: Call<List<SourceOption>>, t: Throwable) {}
                    })
            } else {
                Toast.makeText(
                    requireContext(),
                    "Enter a 16-digit destination card",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnTransfer.setOnClickListener {
            val dest = etDest.text.toString()
            val amt  = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            RetrofitClient.apiService
                .transfer(
                    "Token ${Prefs.getToken(requireContext())}",
                    TransferRequest(selectedSource, dest, amt)
                )
                .enqueue(object : Callback<TransferResponse> {
                    override fun onResponse(
                        call: Call<TransferResponse>,
                        r: Response<TransferResponse>
                    ) {
                        if (r.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Transferred successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            loadAllCards()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Transfer failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    override fun onFailure(c: Call<TransferResponse>, t: Throwable) {}
                })
        }
    }

    private fun loadAllCards() {
        val token = "Token " + Prefs.getToken(requireContext())
        RetrofitClient.apiService.getCards(token)
            .enqueue(object: Callback<List<Card>> {
                override fun onResponse(call: Call<List<Card>>, resp: Response<List<Card>>) {
                    Log.d("MAIN", "getCards code=${resp.code()} body=${resp.body()}")
                    if (resp.isSuccessful) {
                        rvAllCards.adapter = CardAdapter(resp.body()!!)
                    }
                }
                override fun onFailure(call: Call<List<Card>>, t: Throwable) {
                    Log.e("MAIN", "getCards error", t)
                }
            })
    }

    // Inflate the menu with the Logout item
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                Prefs.clear(requireContext())
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}