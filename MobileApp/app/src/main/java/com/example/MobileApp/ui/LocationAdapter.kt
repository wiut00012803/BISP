package com.example.MobileApp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.MobileApp.R
import com.example.MobileApp.model.Location

class LocationAdapter(
    private val items: List<Location>,
    private val cb: (Location)->Unit
): RecyclerView.Adapter<LocationAdapter.VH>() {
    class VH(parent: ViewGroup): RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location,parent,false)
    ) {
        val tv = itemView.findViewById<TextView>(R.id.tvLocation)
    }
    override fun onCreateViewHolder(p: ViewGroup,i:Int)=VH(p).apply {
        itemView.setOnClickListener{ cb(items[bindingAdapterPosition]) }
    }
    override fun getItemCount()=items.size
    override fun onBindViewHolder(h:VH,i:Int){
        h.tv.text = items[i].name
    }
}