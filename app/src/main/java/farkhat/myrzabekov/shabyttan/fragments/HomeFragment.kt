package farkhat.myrzabekov.shabyttan.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import farkhat.myrzabekov.shabyttan.databinding.DialogFullScreenImageBinding
import farkhat.myrzabekov.shabyttan.databinding.FragmentHomeBinding
import farkhat.myrzabekov.shabyttan.viewmodels.FirestoreViewModel
import kotlin.math.abs

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirestoreViewModel by viewModels()
    private var currentDrawable: Drawable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        initializeUI()
        setupObservers()

        return binding.root
    }

    private fun initializeUI() {
        setupAppBarListener()
        setupImageClickToShowDialog()
        setupScrollViewListener()
        setupToTopButton()

        viewModel.fetchArtworkByDate()
    }

    private fun setupToTopButton() {
        binding.toTopActionButton.setOnClickListener {
            binding.nestedScrollView.scrollTo(0, 0)
            binding.appbar.setExpanded(true, true)
            it.visibility = View.INVISIBLE
        }
    }

    private fun setupScrollViewListener() {
        binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            handleScrollChange(scrollY, oldScrollY)
        }
    }

    private fun handleScrollChange(scrollY: Int, oldScrollY: Int) {
        if (scrollY > oldScrollY && !binding.toTopActionButton.isVisible) {
            showToTopButton()
        } else if (scrollY < oldScrollY && binding.toTopActionButton.isVisible) {
            hideToTopButton()
        }
    }

    private fun showToTopButton() {
        binding.toTopActionButton.apply {
            animate().alpha(1.0f).duration = 300
            visibility = View.VISIBLE
        }
    }

    private fun hideToTopButton() {
        binding.toTopActionButton.animate().alpha(0.0f).withEndAction {
            binding.toTopActionButton.visibility = View.GONE
        }.duration = 300
    }

    private fun setupImageClickToShowDialog() {
        binding.artImage.setOnClickListener {
            showDialogWithImage(currentDrawable)
        }
    }

    private fun showDialogWithImage(drawable: Drawable?) {
        val dialog = Dialog(requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = DialogFullScreenImageBinding.inflate(layoutInflater)
            setContentView(dialogBinding.root)
            dialogBinding.fullscreenImage.apply {
                setImageDrawable(drawable)
                setOnClickListener { dismiss() }
                adjustScaleType(drawable)
            }
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.show()
    }

    private fun ImageView.adjustScaleType(drawable: Drawable?) {
        drawable?.let {
            if (it.intrinsicHeight < it.intrinsicWidth) {
                this.scaleType = ImageView.ScaleType.FIT_CENTER
            } else {
                this.scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    private fun setupAppBarListener() {
        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            handleAppBarStateChange(abs(verticalOffset) - appBarLayout.totalScrollRange == 0)
        }
    }

    private fun handleAppBarStateChange(isCollapsed: Boolean) {
        binding.apply {
            val visibility = if (isCollapsed) View.INVISIBLE else View.VISIBLE
            shareActionButton.visibility = visibility
            likeActionButton.visibility = visibility
            artImage.visibility = visibility
        }
    }

    private fun extractArtistName(description: String): String {
        val nameRegex = "^(.+?)\\s*\\(".toRegex()
        val matchResult = nameRegex.find(description)
        return matchResult?.groups?.get(1)?.value ?: description
    }

    private fun setupObservers() {
        viewModel.artworkLiveData.observe(viewLifecycleOwner) { artwork ->
            artwork?.let {
                updateUI(it.title, it.author, it.description, it.funFact, it.imageURL)
            }
        }
    }

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
            loadImage(imageUrl)
            removePlaceholders()
        }
    }

    private fun FragmentHomeBinding.loadImage(imageUrl: String?) {
        Glide.with(requireActivity())
            .load(imageUrl)
            .timeout(60000)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    artImage.setImageDrawable(resource)
                    currentDrawable = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun removePlaceholders() {
        binding.apply {
            artTitle.setBackgroundColor(Color.TRANSPARENT)
            artAuthor.setBackgroundColor(Color.TRANSPARENT)
            artDescription.setBackgroundColor(Color.TRANSPARENT)

            val layoutParams1 = artDescription.layoutParams as LinearLayout.LayoutParams
            layoutParams1.height = LinearLayout.LayoutParams.WRAP_CONTENT
            artDescription.layoutParams = layoutParams1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
