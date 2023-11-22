package farkhat.myrzabekov.shabyttan.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import farkhat.myrzabekov.shabyttan.R
import farkhat.myrzabekov.shabyttan.databinding.DialogFullScreenImageBinding
import farkhat.myrzabekov.shabyttan.databinding.FragmentHomeBinding
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.viewmodels.FirestoreViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirestoreViewModel by viewModels()
    private val uiHelper by lazy { UIHelper(this) }
    private var currentArtwork: Artwork? = null

    private var isArtworkLiked = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializeUI()
        setupObservers()

        setupLikeButton()
        setupShareButton()

        return binding.root
    }

    private fun setupLikeButton() {
        binding.likeActionButton.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val artwork = getCurrentArtwork()
            if (user != null) {
                val userId = user.uid

                isArtworkLiked = !isArtworkLiked

                val heartRes: Int
                if (isArtworkLiked) {
                    heartRes = R.drawable.ic_red_heart
                    ImageViewCompat.setImageTintList(binding.likeActionButton, null)
                } else {
                    heartRes = R.drawable.ic_heart
                    val colorStateList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    ImageViewCompat.setImageTintList(binding.likeActionButton, colorStateList)
                }
                binding.likeActionButton.setImageResource(heartRes)


                viewModel.addArtworkToUserLikes(userId, artwork)
            } else {
                // Handle case when user is not logged in or artwork is null
                // You could disable the like button until an artwork is loaded
                // or prompt the user to log in.
            }
        }
    }

    private fun setupShareButton() {
        binding.shareActionButton.setOnClickListener {
            uiHelper.shareArtworkDeepLink("https://farkhat.myrzabekov.shabyttan/artwork/${currentArtwork?.id}")
        }
    }


    private fun getCurrentArtwork(): Artwork {
        return currentArtwork ?: throw IllegalStateException("No current artwork available")
    }


    private fun initializeUI() {
        uiHelper.setupAppBarListener(
            binding.appbar,
            binding.artImage,
            binding.shareActionButton,
            binding.likeActionButton
        )
        uiHelper.setupImageClickToShowDialog(binding.artImage) {
            DialogFullScreenImageBinding.inflate(layoutInflater)
        }
        uiHelper.setupScrollViewListener(binding.nestedScrollView, binding.toTopActionButton)
        uiHelper.setupToTopButton(
            binding.toTopActionButton,
            binding.nestedScrollView,
            binding.appbar
        )

        viewModel.fetchArtworkByDate()
        viewModel.fetchUserLikedArtworks()
    }

    private fun setupObservers() {
        viewModel.artworkLiveData.observe(viewLifecycleOwner) { artwork ->
            artwork?.let {
                currentArtwork = it
                updateUI(it.title, it.author, it.description, it.funFact, it.imageURL)
            }
        }


    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(
        title: String?,
        author: String?,
        description: String?,
        funFact: String?,
        imageUrl: String?
    ) {
        binding.apply {
            artTitle.text = title
            artAuthor.text = extractArtistName(author ?: "")
            artDescription.text = description
            artFunFact.text = funFact
            uiHelper.loadImage(artImage, imageUrl)
            removePlaceholders()

            if (artAuthor.text.isBlank()) artAuthor.text =
                requireActivity().getString(R.string.author_unknown)
        }

        viewModel.userLikedArtworksLiveData.observe(viewLifecycleOwner) { likedArtworks ->
            isArtworkLiked = likedArtworks.any { art -> art.id == currentArtwork?.id }
            if (isArtworkLiked) {
                binding.likeActionButton.setImageResource(R.drawable.ic_red_heart)
                ImageViewCompat.setImageTintList(binding.likeActionButton, null)
            }
        }
    }

    private fun extractArtistName(description: String): String {
        val nameRegex = "^(.+?)\\s*\\(".toRegex()
        val matchResult = nameRegex.find(description)
        return matchResult?.groups?.get(1)?.value ?: description
    }

    private fun removePlaceholders() {
        binding.apply {
            artTitle.setBackgroundColor(Color.TRANSPARENT)
            artAuthor.setBackgroundColor(Color.TRANSPARENT)
            artDescription.setBackgroundColor(Color.TRANSPARENT)

            val layoutParams = artDescription.layoutParams as LinearLayout.LayoutParams
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            artDescription.layoutParams = layoutParams
        }
    }

    override fun onDestroyView() {
        uiHelper.clearImage(binding.artImage)
        super.onDestroyView()
        _binding = null
    }
}
