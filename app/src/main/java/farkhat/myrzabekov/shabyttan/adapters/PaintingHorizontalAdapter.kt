package farkhat.myrzabekov.shabyttan.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import farkhat.myrzabekov.shabyttan.databinding.ItemPaintingBinding

class PaintingHorizontalAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<PaintingHorizontalAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemPaintingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPaintingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }
}

