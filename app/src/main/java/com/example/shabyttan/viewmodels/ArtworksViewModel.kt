package com.example.shabyttan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shabyttan.models.MuseumData
import com.example.shabyttan.repositories.ArtworksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArtworksViewModel(private val artworksRepository: ArtworksRepository): ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            artworksRepository.getArtworks()
        }
    }

    val artworks : LiveData<MuseumData>
        get() = artworksRepository.artworks

}
