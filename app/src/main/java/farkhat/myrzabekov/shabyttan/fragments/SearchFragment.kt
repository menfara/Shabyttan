package farkhat.myrzabekov.shabyttan.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import farkhat.myrzabekov.shabyttan.adapters.ArtistRecyclerViewAdapter
import farkhat.myrzabekov.shabyttan.adapters.RecommendationRecyclerViewAdapter
import farkhat.myrzabekov.shabyttan.decorations.PageIndicator
import farkhat.myrzabekov.shabyttan.decorations.HorizontalSpaceItemDecoration
import farkhat.myrzabekov.shabyttan.decorations.StartLinearSnapHelper
import farkhat.myrzabekov.shabyttan.decorations.dp
import farkhat.myrzabekov.shabyttan.adapters.HistoryRecyclerViewAdapter
import farkhat.myrzabekov.shabyttan.databinding.FragmentSearchBinding
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.viewmodels.FirestoreViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirestoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)


        setupArtistRecyclerViewUI()
        setupRecommendationRecyclerViewUI()
        setupHistoryRecyclerViewUI()

        loadPlaceholders()


        viewModel.fetchArtworksForLastThreeDays()
        viewModel.fetchRandomArtworks()
        viewModel.fetchArtworksByAuthor("Claude Monet")


        viewModel.artworksHistoryLiveData.observe(viewLifecycleOwner, ::updateHistoryRecyclerView)
        viewModel.randomArtworksLiveData.observe(
            viewLifecycleOwner,
            ::updateRecommendationRecyclerView
        )
        viewModel.artworksByAuthorLiveData.observe(viewLifecycleOwner, ::updateArtistRecyclerView)


        initSearchInput()

        return binding.root
    }

    private fun loadPlaceholders() {
        val emptyArtworkList = List(4) {
            Artwork()
        }

        binding.horizontalRecyclerView.adapter = ArtistRecyclerViewAdapter(emptyArtworkList)
        binding.recommendedRecyclerView.adapter =
            RecommendationRecyclerViewAdapter(emptyArtworkList)
        binding.historyRecyclerView.adapter = HistoryRecyclerViewAdapter(emptyArtworkList)
    }

    private fun setupArtistRecyclerViewUI() {
        binding.horizontalRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.horizontalRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(15.dp))
    }

    private fun setupRecommendationRecyclerViewUI() {
        val layoutManagerRecommended =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recommendedRecyclerView.layoutManager = layoutManagerRecommended
        binding.recommendedRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(15.dp))
        val indicators =
            listOf(binding.indicator1, binding.indicator2, binding.indicator3, binding.indicator4)
        binding.recommendedRecyclerView.addOnScrollListener(
            PageIndicator(
                layoutManagerRecommended,
                indicators
            )
        )
        StartLinearSnapHelper().attachToRecyclerView(binding.recommendedRecyclerView)
    }

    private fun setupHistoryRecyclerViewUI() {
        binding.historyRecyclerView.layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false
        }
    }

    private fun updateArtistRecyclerView(artworks: List<Artwork>) {
        binding.horizontalRecyclerView.adapter = ArtistRecyclerViewAdapter(artworks)
    }

    private fun updateRecommendationRecyclerView(artworks: List<Artwork>) {
        binding.recommendedRecyclerView.adapter = RecommendationRecyclerViewAdapter(artworks)
    }

    private fun updateHistoryRecyclerView(artworks: List<Artwork>) {
        binding.historyRecyclerView.adapter = HistoryRecyclerViewAdapter(artworks)
    }

    private fun initSearchInput() {
        binding.textInputSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                performSearch(v.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(query: String) {
        // Your code to execute search
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun RecyclerView.setupRecyclerView(
        adapter: RecyclerView.Adapter<*>,
        layoutManager: RecyclerView.LayoutManager,
        itemDecoration: RecyclerView.ItemDecoration? = null,
        onScrollListener: RecyclerView.OnScrollListener? = null,
        snapHelper: SnapHelper? = null
    ) {
        this.adapter = adapter
        this.layoutManager = layoutManager
        itemDecoration?.let { addItemDecoration(it) }
        onScrollListener?.let { addOnScrollListener(it) }
        snapHelper?.let { it.attachToRecyclerView(this) }
    }
}
