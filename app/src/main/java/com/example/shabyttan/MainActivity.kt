package com.example.shabyttan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.shabyttan.api.ApiInterface
import com.example.shabyttan.api.ApiUtilities
import com.example.shabyttan.repositories.ArtworksRepository
import com.example.shabyttan.viewmodels.ArtworksViewModel
import com.example.shabyttan.viewmodels.ArtworksViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var artworksViewModel: ArtworksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val apiInterface = ApiUtilities.getInstance().create(ApiInterface::class.java)
//
//        val artworksRepository = ArtworksRepository(apiInterface)
//
//        artworksViewModel = ViewModelProvider(this, ArtworksViewModelFactory(artworksRepository))[ArtworksViewModel::class.java]
//
//        artworksViewModel.artworks.observe(this) {
//            Log.d("MyTag", it.toString())
//            Log.d("MyTag", it.data[0].title)
//        }
    }
}