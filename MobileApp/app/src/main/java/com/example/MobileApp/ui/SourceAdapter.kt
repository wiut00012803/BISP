package com.example.MobileApp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.MobileApp.R
import com.example.MobileApp.model.SourceOption

class SourceAdapter(
    private val items: List<SourceOption>,
    private val cb: (String)->Unit
): RecyclerView.Adapter<SourceAdapter.VH>() {
    class VH(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_source,parent,false)
    ) {
        val tv = itemView.findViewById<TextView>(R.id.tvSource)
    }
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(p).apply {
        itemView.setOnClickListener {
            cb(items[bindingAdapterPosition].card_number)
        }
    }
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: VH, i: Int) {
        h.tv.text = items[i].label
    }
}