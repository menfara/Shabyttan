package farkhat.myrzabekov.shabyttan.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import farkhat.myrzabekov.shabyttan.api.ApiInterface
import farkhat.myrzabekov.shabyttan.api.ApiUtilities
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.models.Data
import farkhat.myrzabekov.shabyttan.models.toArtwork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.random.Random

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()


    // ARTWORKS COLLECTION METHODS

    fun getArtworksByAuthor(authorName: String, callback: (List<Artwork?>) -> Unit) {
        db.collection("Artworks")
            .whereEqualTo("author", authorName)
            .limit(4)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val artworks =
                    querySnapshot.documents.map { adjustArtworkForLocale(it.toObject(Artwork::class.java)) }
                callback(artworks)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreRepository", "Error fetching artworks by author", exception)
            }
    }


    private fun getTotalArtworksCount(): Int {
        return 5
    }

    fun getRandomArtworks(callback: (List<Artwork?>) -> Unit) {
        val randomIndices = mutableSetOf<Int>()
        while (randomIndices.size < 4) {
            randomIndices.add((0 until getTotalArtworksCount()).random())
        }

        db.collection("Artworks").get().addOnSuccessListener { querySnapshot ->
            val randomArtworks =
                randomIndices.map {
                    adjustArtworkForLocale(
                        querySnapshot.documents[it].toObject(
                            Artwork::class.java
                        )
                    )
                }
            callback(randomArtworks)
        }

    }


    private fun addArtwork(artwork: Artwork, completion: (Boolean) -> Unit) {
        db.collection("Artworks").add(artwork)
            .addOnSuccessListener {
                completion(true) // Pass true when the operation is successful
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreRepository", "Error adding document", exception)
                completion(false) // Pass false if there's an error
            }
    }


    fun getArtworkById(artworkId: Long, callback: (Artwork?) -> Unit) {
        db.collection("Artworks")
            .whereEqualTo("id", artworkId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {

                    val artwork = documents.documents.first().toObject(Artwork::class.java)
                    callback(adjustArtworkForLocale(artwork))
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }


    fun getArtworkByDate(date: String, callback: (Artwork?) -> Unit) {
        db.collection("Artworks")
            .whereEqualTo("date", date)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val artwork = querySnapshot.documents[0].toObject(Artwork::class.java)
                    callback(adjustArtworkForLocale(artwork))
                } else {
                    getArtworkBySkip { data, _ ->
                        if (data != null) {
                            val newArtwork = data.toArtwork().copy(date = date)
                            addArtwork(newArtwork) { callback(newArtwork) }
                        } else {
                            callback(null)
                        }
                    }
                }
            }
    }

    private fun getArtworkBySkip(callback: (Data?, Int) -> Unit) {
        val apiInterface = ApiUtilities.getInstance().create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val response = apiInterface.getArtworkBySkip(Random.nextInt(1, 65000))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val data = response.body()?.data?.firstOrNull()
                        callback(data, response.code())
                    } else {
                        Log.e("FirestoreRepository", "Request failed with code ${response.code()}")
                        callback(null, response.code())
                    }
                }
            } catch (e: Exception) {
                Log.e("FirestoreRepository", "Error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    callback(null, -1)
                }
            }
        }
    }

    fun fetchUserLikedArtworks(callback: (List<Artwork>) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            val userLikedArtworksRef = db.collection("Users").document(user.uid).collection("liked_artworks")

            userLikedArtworksRef.get().addOnSuccessListener { documents ->
                val likedArtworks = documents.mapNotNull { it.toObject(Artwork::class.java) }
                callback(likedArtworks)
            }.addOnFailureListener {
                callback(emptyList())
            }
        } ?: run {
            callback(emptyList())
        }
    }
    private fun adjustArtworkForLocale(artwork: Artwork?): Artwork? {
        val isRussian = Locale.getDefault().language.equals("ru", ignoreCase = true)
        return if (isRussian && artwork != null) {
            Artwork(
                id = artwork.id,
                author = artwork.author_ru ?: artwork.author,
                description = artwork.description_ru ?: artwork.description,
                funFact = artwork.funFact_ru ?: artwork.funFact,
                imageURL = artwork.imageURL,
                title = artwork.title_ru ?: artwork.title,
                date = artwork.date,
                likesCount = artwork.likesCount
            )
        } else {
            artwork
        }
    }


}
