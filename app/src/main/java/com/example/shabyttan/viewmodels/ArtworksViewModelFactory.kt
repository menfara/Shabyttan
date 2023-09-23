package com.example.shabyttan.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.shabyttan.repositories.ArtworksRepository

class ArtworksViewModelFactory(private val artworksRepository: ArtworksRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArtworksViewModel(artworksRepository) as T
    }
}