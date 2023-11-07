package farkhat.myrzabekov.shabyttan.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import farkhat.myrzabekov.shabyttan.databinding.ItemPaintingBinding
import farkhat.myrzabekov.shabyttan.models.Artwork
import com.bumptech.glide.Glide
import farkhat.myrzabekov.shabyttan.R
interface OnArtworkClickListener {
    fun onArtworkClick(artwork: Artwork)
}
class ArtistRecyclerViewAdapter(private val artworks: List<Artwork>, private val listener: OnArtworkClickListener) :
    RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder>() {


    inner class ViewHolder(private val binding: ItemPaintingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(artwork: Artwork) {
            Glide.with(binding.imageView.context)
                .load(artwork.imageURL)
                .placeholder(R.drawable.placeholder_image)
                .into(binding.imageView)
            binding.root.setOnClickListener {
                listener.onArtworkClick(artwork)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPaintingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(artworks[position])
    }

    override fun getItemCount() = artworks.size
}
