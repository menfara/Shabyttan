package farkhat.myrzabekov.shabyttan.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import farkhat.myrzabekov.shabyttan.repositories.FirestoreRepository

class FirestoreViewModel : ViewModel() {

    private val repository = FirestoreRepository()
    val artworkLiveData = MutableLiveData<FirestoreRepository.Artwork>()

    fun fetchArtwork() {
        repository.getArtworkDataById("i3mluB7NdGLUx4VEYOma") { artwork ->
            artworkLiveData.postValue(artwork)
        }
    }
}