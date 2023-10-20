package com.example.shabyttan.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.shabyttan.R
import com.example.shabyttan.api.ApiInterface
import com.example.shabyttan.api.ApiUtilities
import com.example.shabyttan.databinding.FragmentHome1Binding
import com.example.shabyttan.repositories.ArtworksRepository
import com.example.shabyttan.viewmodels.ArtworksViewModel
import com.example.shabyttan.viewmodels.ArtworksViewModelFactory
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import kotlin.math.abs

class HomeFragment : Fragment() {
    private var _binding: FragmentHome1Binding? = null
    private val binding get() = _binding!!
    private lateinit var artworksViewModel: ArtworksViewModel
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHome1Binding.inflate(inflater, container, false)
//        initializeViewModel()
//        setupObservers()

        handler.postDelayed({
            setFakeArtTitle()
            removePlaceholders()
        }, 1000)


        setupAppBarListener()
        return binding.root
    }

    private fun setFakeArtTitle() {
        binding.apply {
            artImage.setImageResource(R.drawable.img_hero)
            artTitle.text = requireActivity().getString(R.string.art_title)
            artAuthor.text = requireActivity().getString(R.string.art_author)
            artDescription.text = requireActivity().getString(R.string.art_info)
            artFunFact.text = requireActivity().getString(R.string.fun_fact)

        }
    }

    private fun removePlaceholders() {
        binding.apply {
            artTitle.setBackgroundColor(Color.TRANSPARENT)
            artAuthor.setBackgroundColor(Color.TRANSPARENT)
            artDescription.setBackgroundColor(Color.TRANSPARENT)
            val layoutParams = artAuthor.layoutParams as LinearLayout.LayoutParams
            layoutParams.topMargin = 0
            artAuthor.layoutParams = layoutParams

            val layoutParams1 = artDescription.layoutParams as LinearLayout.LayoutParams
            layoutParams1.height = LinearLayout.LayoutParams.WRAP_CONTENT
            artDescription.layoutParams = layoutParams1

        }

    }

    fun handleShimmerView(shimmerView: ShimmerFrameLayout) {
        val shimmer = Shimmer.AlphaHighlightBuilder().setBaseAlpha(1.0f).build()
        shimmerView.setBackgroundColor(Color.TRANSPARENT)
        shimmerView.setShimmer(shimmer)
        shimmerView.stopShimmer()
    }


    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
        _binding = null
    }

    private fun initializeViewModel() {
        val apiInterface = ApiUtilities.getInstance().create(ApiInterface::class.java)
        val artworksRepository = ArtworksRepository(apiInterface)
        artworksViewModel =
            ViewModelProvider(this, ArtworksViewModelFactory(artworksRepository)).get(
                ArtworksViewModel::class.java
            )
    }

    private fun setupObservers() {
        artworksViewModel.artworks.observe(requireActivity()) { artworkData ->
            val data = artworkData.data[0]
            binding.apply {
                artTitle.text = data.title
                artAuthor.text = extractArtistName(data.creators[0].description)
                artDescription.text = data.description
                artFunFact.text = HtmlCompat.fromHtml(
                    "Fun fact: ${data.did_you_know}",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
                Glide.with(requireActivity())
                    .load(data.images.web.url)
                    .timeout(60000)
                    .into(artImage)
                removePlaceholders()
            }
        }
    }

    private fun setupAppBarListener() {
        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = abs(verticalOffset) - appBarLayout.totalScrollRange == 0
            handleAppBarStateChange(isCollapsed)
        }
    }

    private fun handleAppBarStateChange(isCollapsed: Boolean) {
        if (isCollapsed) {
            Log.d("MyTag", "Collapsed")
            binding.apply {
                shareActionButton.visibility = View.INVISIBLE
                likeActionButton.visibility = View.INVISIBLE
                artImage.visibility = View.INVISIBLE
            }
        } else {
            Log.d("MyTag", "Expanded")
            binding.apply {
                shareActionButton.visibility = View.VISIBLE
                likeActionButton.visibility = View.VISIBLE
                artImage.visibility = View.VISIBLE
            }
        }
    }

    private fun extractArtistName(description: String): String {
        val nameRegex = "^(.+?)\\s*\\(".toRegex()
        val matchResult = nameRegex.find(description)
        return matchResult?.groups?.get(1)?.value ?: description
    }
}
