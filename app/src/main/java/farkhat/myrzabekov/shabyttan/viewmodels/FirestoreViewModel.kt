package farkhat.myrzabekov.shabyttan.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.repositories.FirestoreRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirestoreViewModel : ViewModel() {

    companion object {
        private const val DATE_FORMAT = "dd/MM/yyyy"
        private const val LOCALE_LANGUAGE = "ru"
        private const val LOCALE_COUNTRY = "KZ"
    }

    private val repository = FirestoreRepository()
    val artworkLiveData = MutableLiveData<Artwork>()
    val artworksHistoryLiveData = MutableLiveData<List<Artwork>>()
    val randomArtworksLiveData = MutableLiveData<List<Artwork>>()
    val artworksByAuthorLiveData = MutableLiveData<List<Artwork>>()
    private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale(LOCALE_LANGUAGE, LOCALE_COUNTRY))


    private var cachedRandomArtworks: List<Artwork>? = null

    fun fetchArtworkById(id: String = "pTqDvyL326VJL2miVyp7") {
        repository.getArtworkById(id) { artwork ->
            artworkLiveData.postValue(artwork)
        }
    }


    fun fetchArtworksByAuthor(authorName: String) {
        repository.getArtworksByAuthor(authorName) { artworks ->
            artworksByAuthorLiveData.postValue(artworks.filterNotNull())
        }
    }

    fun fetchArtworkByDate(date: Date = Date()) {
//        val dateString = dateFormat.format(date)
        val dateString = dateFormat.format(getFixedDate())
        repository.getArtworkByDate(dateString) { artwork ->
            artworkLiveData.postValue(artwork)
        }
    }

    fun fetchArtworksForLastThreeDays() {
        val artworksList = mutableListOf<Artwork>()
        for (i in 0..2) {
            // Date()
            val dateToFetch = subtractDays(getFixedDate(), i)
            val dateString = dateFormat.format(dateToFetch)
            repository.getArtworkByDate(dateString) { artwork ->
                artwork?.let { artworksList.add(it) }
                if (artworksList.size == 3) artworksHistoryLiveData.postValue(artworksList)
            }
        }
    }


    private fun subtractDays(date: Date, days: Int): Date {
        return Date(date.time - days * 24 * 60 * 60 * 1000)
    }

    private fun getFixedDate(): Date {
        val fixedDate = "31/10/2023"
        return dateFormat.parse(fixedDate)!!
    }

    fun fetchRandomArtworks() {
        cachedRandomArtworks?.let {
            if (it.isNotEmpty()) {
                randomArtworksLiveData.postValue(it)
                return
            }
        }
        repository.getRandomArtworks { artworks ->
            val filteredArtworks = artworks.filterNotNull()
            cachedRandomArtworks = filteredArtworks
            randomArtworksLiveData.postValue(filteredArtworks)
        }
    }
}
