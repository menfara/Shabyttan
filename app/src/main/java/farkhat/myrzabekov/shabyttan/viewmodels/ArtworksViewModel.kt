package farkhat.myrzabekov.shabyttan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import farkhat.myrzabekov.shabyttan.models.MuseumData
import farkhat.myrzabekov.shabyttan.repositories.ArtworksRepository
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
