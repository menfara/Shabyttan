package farkhat.myrzabekov.shabyttan.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import farkhat.myrzabekov.shabyttan.databinding.DialogFullScreenImageBinding
import kotlin.math.abs
class UIHelper(private val fragment: Fragment) {
    private var currentDrawable: Drawable? = null


    fun setupAppBarListener(appBarLayout: AppBarLayout, imageView: ImageView, vararg additionalViews: View) {
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val isCollapsed = abs(verticalOffset) - appBarLayout.totalScrollRange == 0
            val visibility = if (isCollapsed) View.INVISIBLE else View.VISIBLE
            imageView.visibility = visibility
            additionalViews.forEach { it.visibility = visibility }
        })
    }

    fun setupToTopButton(toTopActionButton: View, scrollView: NestedScrollView, appBarLayout: AppBarLayout) {
        toTopActionButton.setOnClickListener {
            scrollView.scrollTo(0, 0)
            appBarLayout.setExpanded(true, true)
            it.visibility = View.INVISIBLE
        }
    }

    fun setupScrollViewListener(scrollView: NestedScrollView, toTopActionButton: View) {
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY && !toTopActionButton.isVisible) {
                showToTopButton(toTopActionButton)
            } else if (scrollY < oldScrollY && toTopActionButton.isVisible) {
                hideToTopButton(toTopActionButton)
            }
        }
    }

    private fun showToTopButton(toTopActionButton: View) {
        toTopActionButton.apply {
            animate().alpha(1.0f).duration = 300
            visibility = View.VISIBLE
        }
    }

    private fun hideToTopButton(toTopActionButton: View) {
        toTopActionButton.animate().alpha(0.0f).withEndAction {
            toTopActionButton.visibility = View.GONE
        }.duration = 300
    }

    fun setupImageClickToShowDialog(imageView: ImageView, dialogBindingInflater: () -> DialogFullScreenImageBinding) {
        imageView.setOnClickListener {
            showDialogWithImage(currentDrawable, dialogBindingInflater)
        }
    }

    private fun showDialogWithImage(drawable: Drawable?, dialogBindingInflater: () -> DialogFullScreenImageBinding) {
        val dialog = Dialog(fragment.requireContext()).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = dialogBindingInflater()
            setContentView(dialogBinding.root)
            dialogBinding.fullscreenImage.apply {
                setImageDrawable(drawable)
                setOnClickListener { dismiss() }
            }
            dialogBinding.fullscreenImage.onTapListener = {
                this.dismiss() // Закрываем диалог при тапе
            }

            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog.show()
    }

    fun loadImage(imageView: ImageView, imageUrl: String?) {
        imageUrl?.let { url ->
            Glide.with(fragment)
                .load(url)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        imageView.setImageDrawable(resource)
                        currentDrawable = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle the case when Glide is no longer using the image
                    }
                })
        }
    }

    fun clearImage(imageView: ImageView) {
        Glide.with(fragment).clear(imageView)
    }

    fun shareArtworkDeepLink(artworkLink: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, artworkLink)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(fragment.requireContext(), shareIntent, null)
    }
}
