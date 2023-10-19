package com.example.shabyttan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.shabyttan.databinding.ItemHistoryBinding
import com.example.shabyttan.models.Painting

class PaintingAdapter(private val paintings: List<Painting>) :
    RecyclerView.Adapter<PaintingAdapter.PaintingViewHolder>() {

    inner class PaintingViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(painting: Painting) {
            binding.apply {
                title.text = painting.title
                author.text = painting.author
                date.text = painting.date
                imageViewHistory.setImageResource(painting.imageResId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaintingViewHolder {
        val binding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaintingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaintingViewHolder, position: Int) {
        holder.bind(paintings[position])
    }

    override fun getItemCount() = paintings.size
}
