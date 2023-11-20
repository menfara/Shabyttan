package farkhat.myrzabekov.shabyttan.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.databinding.ItemHistoryBinding
import com.bumptech.glide.Glide
import farkhat.myrzabekov.shabyttan.R

class HistoryRecyclerViewAdapter(private val artworks: List<Artwork>, private val listener: OnArtworkClickListener) :
    RecyclerView.Adapter<HistoryRecyclerViewAdapter.PaintingViewHolder>() {

    inner class PaintingViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(artwork: Artwork) {
            binding.apply {
                title.text = artwork.title
                author.text = artwork.author
                if (author.text.isBlank()) author.text = itemView.context.getString(R.string.author_unknown)
//                date.text = artwork.date

                Glide.with(imageViewHistory.context)
                    .load(artwork.imageURL)
                    .placeholder(R.drawable.placeholder_image)
                    .into(imageViewHistory)
                binding.root.setOnClickListener {
                    listener.onArtworkClick(artwork)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaintingViewHolder {
        val binding =
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaintingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaintingViewHolder, position: Int) {
        holder.bind(artworks[position])
    }

    override fun getItemCount() = artworks.size
}
