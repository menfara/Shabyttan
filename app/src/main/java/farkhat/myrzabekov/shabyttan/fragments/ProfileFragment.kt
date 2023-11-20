package farkhat.myrzabekov.shabyttan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import farkhat.myrzabekov.shabyttan.R
import farkhat.myrzabekov.shabyttan.adapters.HistoryRecyclerViewAdapter
import farkhat.myrzabekov.shabyttan.adapters.OnArtworkClickListener
import farkhat.myrzabekov.shabyttan.databinding.FragmentProfileBinding
import farkhat.myrzabekov.shabyttan.databinding.FragmentSearchBinding
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.viewmodels.FirestoreViewModel


class ProfileFragment : Fragment(), OnArtworkClickListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirestoreViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val emptyArtworkList = List(4) {
            Artwork()
        }
        binding.historyRecyclerView.adapter = HistoryRecyclerViewAdapter(emptyArtworkList, this)
        binding.historyRecyclerView.layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false
        }
        viewModel.fetchUserLikedArtworks()

        viewModel.userLikedArtworksLiveData.observe(viewLifecycleOwner, ::updateHistoryRecyclerView)
        return binding.root
    }


    private fun updateHistoryRecyclerView(artworks: List<Artwork>) {
        binding.historyRecyclerView.adapter = HistoryRecyclerViewAdapter(artworks, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onArtworkClick(artwork: Artwork) {
        val bottomSheetFragment = ArtworkBottomSheetFragment.newInstance(
            artwork.id,
            artwork.title,
            artwork.author,
            artwork.description,
            artwork.funFact,
            artwork.imageURL
        )
        bottomSheetFragment.show(childFragmentManager, "ArtworkBottomSheetTag")
    }
}