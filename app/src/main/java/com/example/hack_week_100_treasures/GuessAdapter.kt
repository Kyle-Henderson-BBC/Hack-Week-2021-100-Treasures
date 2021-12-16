package com.example.hack_week_100_treasures

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView

class GuessAdapter(private val data: List<Character>): RecyclerView.Adapter<GuessAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_scroll_item_image)
        private val txtView: TextView = itemView.findViewById(R.id.image_scroll_item_text)
        private val positionView: TextView = itemView.findViewById(R.id.image_position)

        fun bind(char: Character, position: Int, listSize: Int){
            txtView.text = char.name
            val str = "${position + 1} of $listSize"
            positionView.text = str
            char.imageId?.let {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        imageView.context,
                        it
                    )
                )
                txtView.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.image_scroll_item, parent, false)
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