package com.example.shabyttan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.shabyttan.databinding.ItemRecommendedBinding
import com.example.shabyttan.models.Painting

class RecommendationAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder>() {
    class RecommendationViewHolder(private val binding: ItemRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imageViewRecommended
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        val binding =
            ItemRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecommendationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

}