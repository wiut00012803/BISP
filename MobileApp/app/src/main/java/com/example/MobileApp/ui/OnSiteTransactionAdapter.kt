package com.example.MobileApp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.MobileApp.R
import com.example.MobileApp.model.OnSiteTransaction

class OnSiteTransactionAdapter(
    private val items: List<OnSiteTransaction>
): RecyclerView.Adapter<OnSiteTransactionAdapter.VH>() {
    class VH(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onsite_transaction,parent,false)
    ) {
        val tv = itemView.findViewById<TextView>(R.id.tvOnSiteTx)
    }
    override fun onCreateViewHolder(p: ViewGroup,i:Int)=VH(p)
    override fun getItemCount()=items.size
    override fun onBindViewHolder(h:VH,i:Int){
        val t=items[i]
        h.tv.text = "${t.timestamp}: ${t.card_number}@${t.location} via ${t.platform} | ${t.amount} → CB:${t.cashback_amount}"
    }
}