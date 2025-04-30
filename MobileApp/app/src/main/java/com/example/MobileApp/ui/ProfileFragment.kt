package com.example.MobileApp.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MobileApp.R
import com.example.MobileApp.model.Card
import com.example.MobileApp.network.RetrofitClient
import com.example.MobileApp.util.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private lateinit var rvProfile: RecyclerView
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.fragment_profile, c, false)
    override fun onViewCreated(v: View, s: Bundle?) {
        rvProfile = v.findViewById(R.id.rvProfileCards)
        rvProfile.layoutManager = LinearLayoutManager(requireContext())
        RetrofitClient.apiService
            .getCards(Prefs.getToken(requireContext())!!)
            .enqueue(object: Callback<List<Card>>{
                override fun onResponse(c:Call<List<Card>>,r:Response<List<Card>>){
                    if(r.isSuccessful){
                        rvProfile.adapter = CardAdapter(r.body()!!)
                    }
                }
                override fun onFailure(c:Call<List<Card>>,t:Throwable){}
            })
    }
}