package com.example.MobileApp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.MobileApp.R
import com.example.MobileApp.model.Transaction

class TransactionAdapter(private val items: List<Transaction>)
    : RecyclerView.Adapter<TransactionAdapter.VH>() {

    class VH(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
    ) {
        val tv = itemView.findViewById<TextView>(R.id.tvTransaction)
    }

    override fun onCreateViewHolder(p: ViewGroup, i: Int) = VH(p)
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, i: Int) {
        val t = items[i]
        h.tv.text = "${t.timestamp}: ${t.source_card_number} → ${t.destination_card_number} | " +
                "Amt:${t.amount} | Fee:${t.fee}"
    }
}