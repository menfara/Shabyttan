package com.example.shabyttan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.shabyttan.api.ApiInterface
import com.example.shabyttan.api.ApiUtilities
import com.example.shabyttan.databinding.ActivityMainBinding
import com.example.shabyttan.repositories.ArtworksRepository
import com.example.shabyttan.viewmodels.ArtworksViewModel
import com.example.shabyttan.viewmodels.ArtworksViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var artworksViewModel: ArtworksViewModel

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        bottomNavigationView = binding.bottomnavigation

        bottomNavigationView.setupWithNavController(navController)


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