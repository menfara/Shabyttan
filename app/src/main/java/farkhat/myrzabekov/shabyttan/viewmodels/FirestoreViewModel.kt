package farkhat.myrzabekov.shabyttan.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.repositories.ArtworksRepository
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

    private val firestoreRepository = FirestoreRepository()
    val artworkLiveData = MutableLiveData<Artwork>()
    val artworksHistoryLiveData = MutableLiveData<List<Artwork>>()
    val randomArtworksLiveData = MutableLiveData<List<Artwork>>()
    val artworksByAuthorLiveData = MutableLiveData<List<Artwork>>()
    private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale(LOCALE_LANGUAGE, LOCALE_COUNTRY))
    val userLikedArtworksLiveData = MutableLiveData<List<Artwork>>()


    private var cachedRandomArtworks: List<Artwork>? = null

    fun fetchArtworkById(id: String = "pTqDvyL326VJL2miVyp7") {
        firestoreRepository.getArtworkById(id) { artwork ->
            artworkLiveData.postValue(artwork)
        }
    }


    fun fetchArtworksByAuthor(authorName: String) {
        firestoreRepository.getArtworksByAuthor(authorName) { artworks ->
            artworksByAuthorLiveData.postValue(artworks.filterNotNull())
        }
    }

    fun fetchArtworkByDate(date: Date = Date()) {
        val dateString = dateFormat.format(date)
//        val dateString = dateFormat.format(getFixedDate())
        firestoreRepository.getArtworkByDate(dateString) { artwork ->
            artworkLiveData.postValue(artwork)
        }
    }

    fun fetchArtworksForLastThreeDays() {
        val artworksList = mutableListOf<Artwork>()
        for (i in 1..3) {
            val dateToFetch = subtractDays(Date(), i)
            val dateString = dateFormat.format(dateToFetch)
            firestoreRepository.getArtworkByDate(dateString) { artwork ->
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
        firestoreRepository.getRandomArtworks { artworks ->
            val filteredArtworks = artworks.filterNotNull()
            cachedRandomArtworks = filteredArtworks
            randomArtworksLiveData.postValue(filteredArtworks)
        }
    }

    fun addArtworkToUserLikes(userId: String, artwork: Artwork) {
        val db = FirebaseFirestore.getInstance()
        val userLikesRef = db.collection("Users").document(userId).collection("liked_artworks")
        userLikesRef.add(artwork)
            .addOnSuccessListener {
                // Handle success (e.g., show a toast message that the artwork was liked)
            }
            .addOnFailureListener {
                // Handle error (e.g., show a toast message with the error)
            }
    }

    fun fetchUserLikedArtworks() {
        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            val db = FirebaseFirestore.getInstance()
            val userLikedArtworksRef =
                db.collection("Users").document(user.uid).collection("liked_artworks")

            userLikedArtworksRef.get().addOnSuccessListener { documents ->
                val likedArtworks = documents.mapNotNull { it.toObject(Artwork::class.java) }
                userLikedArtworksLiveData.postValue(likedArtworks) // Save the liked artworks in LiveData
            }.addOnFailureListener {
                // Handle any errors here
            }
        } ?: run {
            userLikedArtworksLiveData.postValue(emptyList())
        }

    }

}
