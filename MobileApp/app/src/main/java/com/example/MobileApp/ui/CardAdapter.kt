package com.example.MobileApp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.MobileApp.R
import com.example.MobileApp.model.Card

class CardAdapter(private val items: List<Card>) : RecyclerView.Adapter<CardAdapter.VH>() {
    class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
    ) {
        val tv = itemView.findViewById<TextView>(R.id.tvCard)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(p)
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, i: Int) {
        val c = items[i]
        h.tv.text = "${c.platform} – ${c.card_type} (${c.bank})\n" +
                "No:${c.card_number}\n" +
                "Bal:${c.balance}  CB:${c.cashback}  Com:${c.commission}%"
    }
}