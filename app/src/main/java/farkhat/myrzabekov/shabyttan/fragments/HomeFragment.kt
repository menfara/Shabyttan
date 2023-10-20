package farkhat.myrzabekov.shabyttan.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import farkhat.myrzabekov.shabyttan.R
import farkhat.myrzabekov.shabyttan.databinding.DialogFullScreenImageBinding
import farkhat.myrzabekov.shabyttan.databinding.FragmentHome1Binding
import farkhat.myrzabekov.shabyttan.viewmodels.FirestoreViewModel
import kotlin.math.abs

class HomeFragment : Fragment() {
    private var _binding: FragmentHome1Binding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private val viewModel: FirestoreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHome1Binding.inflate(inflater, container, false)
//        initializeViewModel()
//        setupObservers()


        viewModel.fetchArtwork()
        setupObservers()

//        setFakeArtTitle()
        setupAppBarListener()
        setupImageClickToShowDialog()




        return binding.root
    }

    private fun setupImageClickToShowDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_full_screen_image)

        binding.artImage.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

            val dialogBinding = DialogFullScreenImageBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.fullscreenImage.setImageDrawable(binding.artImage.drawable)

            val window = dialog.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            dialogBinding.fullscreenImage.setOnClickListener { dialog.dismiss() }

            dialog.show()
        }
    }

    private fun setFakeArtTitle() {
        handler.postDelayed({
            binding.apply {
                artImage.setImageResource(R.drawable.img_hero)
                artTitle.text = requireActivity().getString(R.string.art_title)
                artAuthor.text = requireActivity().getString(R.string.art_author)
                artDescription.text = requireActivity().getString(R.string.art_info)
                artFunFact.text = requireActivity().getString(R.string.fun_fact)

            }
            removePlaceholders()
        }, 1000)

    }

    private fun removePlaceholders() {
        binding.apply {
            artTitle.setBackgroundColor(Color.TRANSPARENT)
            artAuthor.setBackgroundColor(Color.TRANSPARENT)
            artDescription.setBackgroundColor(Color.TRANSPARENT)
//            val layoutParams = artAuthor.layoutParams as LinearLayout.LayoutParams
//            layoutParams.topMargin = 0
//            artAuthor.layoutParams = layoutParams

            val layoutParams1 = artDescription.layoutParams as LinearLayout.LayoutParams
            layoutParams1.height = LinearLayout.LayoutParams.WRAP_CONTENT
            artDescription.layoutParams = layoutParams1

        }

    }


    override fun onDestroyView() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroyView()
        _binding = null
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

    private fun setupObservers() {
        viewModel.artworkLiveData.observe(viewLifecycleOwner, Observer { artwork ->
            artwork?.let {
                Log.d("MyTag", it.toString())
                updateUI(it.title, it.author, it.description, it.funFact, it.imageUrl)
            } ?: run {
                // Handle the case when the artwork is null (maybe show an error message).
            }
        })
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

            Glide.with(requireActivity())
                .load(imageUrl)
                .timeout(60000)
                .into(artImage)

            removePlaceholders()
        }
    }
}
