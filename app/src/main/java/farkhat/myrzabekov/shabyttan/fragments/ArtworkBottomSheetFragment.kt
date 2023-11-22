package farkhat.myrzabekov.shabyttan.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import farkhat.myrzabekov.shabyttan.R
import farkhat.myrzabekov.shabyttan.databinding.DialogFullScreenImageBinding
import farkhat.myrzabekov.shabyttan.databinding.FragmentArtworkBottomSheetBinding
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.viewmodels.FirestoreViewModel

class ArtworkBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentArtworkBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val uiHelper by lazy { UIHelper(this) }
    private var currentArtwork: Artwork? = null
    private val viewModel: FirestoreViewModel by viewModels()
    private var isArtworkLiked = false

    companion object {
        private const val ARG_ID = "argId"
        private const val ARG_TITLE = "argTitle"
        private const val ARG_AUTHOR = "argAuthor"
        private const val ARG_DESCRIPTION = "argDescription"
        private const val ARG_FUN_FACT = "argFunFact"
        private const val ARG_IMAGE_URL = "argImageUrl"

        fun newInstance(
            id: Long?,
            title: String?,
            author: String?,
            description: String?,
            funFact: String?,
            imageUrl: String?
        ) = ArtworkBottomSheetFragment().apply {
            arguments = Bundle().apply {
                if (id != null) {
                    putLong(ARG_ID, id)
                }
                putString(ARG_TITLE, title)
                putString(ARG_AUTHOR, author)
                putString(ARG_DESCRIPTION, description)
                putString(ARG_FUN_FACT, funFact)
                putString(ARG_IMAGE_URL, imageUrl)
            }
        }
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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtworkBottomSheetBinding.inflate(inflater, container, false)
        viewModel.fetchUserLikedArtworks()
        initializeUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }

        // Extract the artwork information from the fragment's arguments
        val id = arguments?.getLong(ARG_ID)
        val title = arguments?.getString(ARG_TITLE)
        val author = arguments?.getString(ARG_AUTHOR)
        val description = arguments?.getString(ARG_DESCRIPTION)
        val funFact = arguments?.getString(ARG_FUN_FACT)
        val imageUrl = arguments?.getString(ARG_IMAGE_URL)

        // Construct the currentArtwork from the arguments
        currentArtwork = Artwork(
            id = id,
            title = title.orEmpty(),
            author = author.orEmpty(),
            description = description.orEmpty(),
            funFact = funFact.orEmpty(),
            imageURL = imageUrl.orEmpty()
        )

        // Update the UI with the extracted artwork information
        updateUI(title, author, description, funFact, imageUrl)

        setupLikeButton()
        setupShareButton()
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
    }

    private fun updateUI(
        title: String?,
        author: String?,
        description: String?,
        funFact: String?,
        imageUrl: String?
    ) {
        with(binding) {
            artTitle.text = title.orEmpty()
            artAuthor.text = author.orEmpty()
            artDescription.text = description.orEmpty()
            artFunFact.text = funFact.orEmpty()
            imageUrl?.let { uiHelper.loadImage(artImage, it) }

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

    override fun onDestroyView() {
        uiHelper.clearImage(binding.artImage)
        super.onDestroyView()
        _binding = null
    }
}
