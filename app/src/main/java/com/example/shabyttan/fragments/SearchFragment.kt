package com.example.shabyttan.fragments

import android.content.Context
import android.os.Bundle
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
import com.example.shabyttan.decorations.SpaceItemDecoration
import com.example.shabyttan.decorations.StartLinearSnapHelper
import com.example.shabyttan.decorations.dp
import com.example.shabyttan.models.Painting


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val images = listOf(R.drawable.painting_1, R.drawable.painting_2, R.drawable.painting_3)
        val adapter = PaintingHorizontalAdapter(images)
        val recyclerView = binding.horizontalRecyclerView

        val paintings = listOf(
            Painting(
                imageResId = R.drawable.history_1,
                title = "Farmhouse in a Wheatfield",
                author = "Vincent van Gogh",
                date = "May 1888"
            ),
            Painting(
                imageResId = R.drawable.history_2,
                title = "The Old Church Tower at Nuenen ('The Peasants' Churchyard')",
                author = "Vincent van Gogh",
                date = "May-June 1885"
            ),
            Painting(
                imageResId = R.drawable.history_3,
                title = "The Pink Orchard",
                author = "Vincent van Gogh",
                date = "April 1888"
            )
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        val adapterHistory = PaintingAdapter(paintings)
        val recyclerViewHistory = binding.historyRecyclerView

        recyclerViewHistory.adapter = adapterHistory
        val myLinearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        recyclerViewHistory.layoutManager = myLinearLayoutManager


        val imagesRecommendation =
            listOf(R.drawable.recommended_1, R.drawable.recommended_2, R.drawable.recommended_3)

        val adapterRecommendation = RecommendationAdapter(imagesRecommendation)
        val recyclerViewRecommendation = binding.recommendedRecyclerView

        recyclerViewRecommendation.adapter = adapterRecommendation
        recyclerViewRecommendation.addItemDecoration(SpaceItemDecoration(15.dp))

        val snapHelper = StartLinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewRecommendation)

        val indicators = listOf(
            binding.indicator1,
            binding.indicator2,
            binding.indicator3,
            binding.indicator4
        )


        val layoutManagerRecommended =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewRecommendation.layoutManager = layoutManagerRecommended
        recyclerViewRecommendation.addOnScrollListener(
            PageIndicator(
                layoutManagerRecommended,
                indicators
            )
        )




        binding.textInputSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)

                performSearch(v.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }


        return binding.root
    }

    fun performSearch(query: String) {
        // Ваш код для выполнения поиска
        // Например, отобразите результаты поиска или отправьте запрос на сервер
    }

}