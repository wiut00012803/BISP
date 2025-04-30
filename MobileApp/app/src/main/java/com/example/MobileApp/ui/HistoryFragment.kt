package com.example.MobileApp.ui

import android.os.Bundle
import android.view.*
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

class HistoryFragment : Fragment() {
    private lateinit var rvTx: RecyclerView
    private lateinit var rvOnSite: RecyclerView
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.fragment_history, c, false)
    override fun onViewCreated(v: View, s: Bundle?) {
        rvTx      = v.findViewById(R.id.rvTransactions)
        rvOnSite  = v.findViewById(R.id.rvOnSiteHistory)
        rvTx.layoutManager     = LinearLayoutManager(requireContext())
        rvOnSite.layoutManager = LinearLayoutManager(requireContext())
        val token = Prefs.getToken(requireContext())!!
        RetrofitClient.apiService
            .getTransactions(token)
            .enqueue(object: Callback<List<Transaction>>{
                override fun onResponse(c:Call<List<Transaction>>,r:Response<List<Transaction>>){
                    if(r.isSuccessful){
                        rvTx.adapter = TransactionAdapter(r.body()!!)
                    }
                }
                override fun onFailure(c:Call<List<Transaction>>,t:Throwable){}
            })
        RetrofitClient.apiService
            .getOnSiteHistory(token)
            .enqueue(object: Callback<List<OnSiteTransaction>>{
                override fun onResponse(c:Call<List<OnSiteTransaction>>,r:Response<List<OnSiteTransaction>>){
                    if(r.isSuccessful){
                        rvOnSite.adapter = OnSiteTransactionAdapter(r.body()!!)
                    }
                }
                override fun onFailure(c:Call<List<OnSiteTransaction>>,t:Throwable){}
            })
    }
}