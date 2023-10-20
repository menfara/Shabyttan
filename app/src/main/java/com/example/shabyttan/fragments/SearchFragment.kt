package com.example.shabyttan.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shabyttan.R
import com.example.shabyttan.adapters.PaintingAdapter
import com.example.shabyttan.adapters.PaintingHorizontalAdapter
import com.example.shabyttan.adapters.RecommendationAdapter
import com.example.shabyttan.databinding.FragmentSearchBinding
import com.example.shabyttan.decorations.PageIndicator
import com.example.shabyttan.decorations.HorizontalSpaceItemDecoration
import com.example.shabyttan.decorations.StartLinearSnapHelper
import com.example.shabyttan.decorations.dp
import com.example.shabyttan.models.Painting

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        populateWithDummyData()
        handler.postDelayed({
            initHorizontalRecyclerView()
            initHistoryRecyclerView()
            initRecommendedRecyclerView()
            initSearchInput()
        }, 1000)

        return binding.root
    }

    private fun populateWithDummyData() {
        val dummyImages = List(5) { R.drawable.placeholder_image }
        binding.horizontalRecyclerView.adapter = PaintingHorizontalAdapter(dummyImages)
        binding.horizontalRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(15.dp)) // Adding space decoration here

        val dummyPaintings = List(3) {
            Painting(
                R.drawable.placeholder_card_15,
                "",
                "",
                ""
            )
        }
        binding.historyRecyclerView.adapter = PaintingAdapter(dummyPaintings)

        val dummyRecommendations = List(5) { R.drawable.placeholder_image }
        binding.recommendedRecyclerView.adapter = RecommendationAdapter(dummyRecommendations)
        binding.recommendedRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(15.dp))
    }


    private fun initHorizontalRecyclerView() {
        val images = listOf(R.drawable.painting_1, R.drawable.painting_2, R.drawable.painting_3)
        binding.horizontalRecyclerView.apply {
            adapter = PaintingHorizontalAdapter(images)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initHistoryRecyclerView() {
        val paintings = listOf(
            Painting(
                R.drawable.history_1,
                "Farmhouse in a Wheatfield",
                "Vincent van Gogh",
                "May 1888"
            ),
            Painting(
                R.drawable.history_2,
                "The Old Church Tower at Nuenen ('The Peasants' Churchyard')",
                "Vincent van Gogh",
                "May-June 1885"
            ),
            Painting(R.drawable.history_3, "The Pink Orchard", "Vincent van Gogh", "April 1888")
        )

        binding.historyRecyclerView.apply {
            adapter = PaintingAdapter(paintings)
            layoutManager = object : LinearLayoutManager(requireContext()) {
                override fun canScrollVertically(): Boolean = false
            }
        }
    }

    private fun initRecommendedRecyclerView() {
        val imagesRecommendation =
            listOf(R.drawable.recommended_1, R.drawable.recommended_2, R.drawable.recommended_3)
        val indicators =
            listOf(binding.indicator1, binding.indicator2, binding.indicator3, binding.indicator4)
        val layoutManagerRecommended =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.recommendedRecyclerView.apply {
            adapter = RecommendationAdapter(imagesRecommendation)
//            addItemDecoration(SpaceItemDecoration(15.dp))
            layoutManager = layoutManagerRecommended
            addOnScrollListener(PageIndicator(layoutManagerRecommended, indicators))

            StartLinearSnapHelper().attachToRecyclerView(this)
        }
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
        // Your code to execute search, e.g., display search results or send a request to a server.
    }

    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
        _binding = null
    }

}
