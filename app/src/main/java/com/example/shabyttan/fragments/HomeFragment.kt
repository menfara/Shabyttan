package com.example.shabyttan.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.shabyttan.R
import com.example.shabyttan.databinding.FragmentHome1Binding
import kotlin.math.abs


class HomeFragment : Fragment() {
    private var _binding: FragmentHome1Binding? = null


    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHome1Binding.inflate(inflater, container, false)


        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->

            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                Log.d("MyTag", "Collapsed")
                binding.apply {
                    shareActionButton.visibility = View.INVISIBLE
                    likeActionButton.visibility = View.INVISIBLE
                    artworkImage.visibility = View.INVISIBLE
                }

            } else {
                Log.d("MyTag", "Expanded")
                binding.apply {
                    shareActionButton.visibility = View.VISIBLE
                    likeActionButton.visibility = View.VISIBLE
                    artworkImage.visibility = View.VISIBLE
                }

            }
        }
        return binding.root
    }


}