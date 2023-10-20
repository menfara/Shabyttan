package farkhat.myrzabekov.shabyttan.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import farkhat.myrzabekov.shabyttan.repositories.ArtworksRepository

class ArtworksViewModelFactory(private val artworksRepository: ArtworksRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArtworksViewModel(artworksRepository) as T
    }
}