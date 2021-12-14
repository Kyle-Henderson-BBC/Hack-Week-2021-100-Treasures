package com.example.hack_week_100_treasures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainAdapter(private val data: List<String>): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtView: TextView = itemView.findViewById(R.id.scroll_item_text)
        private val positionView: TextView = itemView.findViewById(R.id.position)

        fun bind(txt: String, position: Int, listSize: Int){
            txtView.text = txt
            val str = "${position + 1} of $listSize"
            positionView.text = str
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.scroll_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data.getOrNull(position)?.let{
            holder.bind(it, position, itemCount)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}