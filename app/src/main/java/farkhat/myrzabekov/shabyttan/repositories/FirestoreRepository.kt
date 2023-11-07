package farkhat.myrzabekov.shabyttan.repositories

import android.util.Log
import com.google.firebase.firestore.FieldValue
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
import kotlin.random.Random

class FirestoreRepository {

    companion object {
        private const val ARTWORKS_COLLECTION = "Artworks"
        private const val USERS_COLLECTION = "Users"
        private const val AUTHOR_FIELD = "author"
        private const val LIKES_COUNT_FIELD = "likesCount"
        private const val LIKED_ARTWORKS_FIELD = "likedArtworks"
        private const val DATE_FIELD = "date"
    }

    private val db = FirebaseFirestore.getInstance()

    data class User(
        val userId: String? = null,
        val likedArtworks: List<String> = listOf()
    )


    // ARTWORKS COLLECTION METHODS

    fun getArtworksByAuthor(authorName: String, callback: (List<Artwork?>) -> Unit) {
        db.collection("Artworks")
            .whereEqualTo("author", authorName)
            .limit(4)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val artworks = querySnapshot.documents.map { it.toObject(Artwork::class.java) }
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
                randomIndices.map { querySnapshot.documents[it].toObject(Artwork::class.java) }
            callback(randomArtworks)
        }

    }


    fun addArtwork(artwork: Artwork) {
        db.collection("Artworks").add(artwork)
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


    fun getArtworkById(documentId: String, callback: (Artwork?) -> Unit) {
        db.collection("Artworks").document(documentId).get().addOnSuccessListener { document ->
            callback(document.toObject(Artwork::class.java))
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
                    callback(artwork)
                } else {
                    getArtworkBySkip() { data, code ->
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

                val response = apiInterface.getArtworkBySkip(Random.nextInt(1, 1001))
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

    fun incrementArtworkLikes(documentId: String) {
        db.collection("Artworks").document(documentId).update("likesCount", FieldValue.increment(1))
    }

    fun deleteArtworkById(documentId: String) {
        db.collection("Artworks").document(documentId).delete()
    }


    fun addUser(user: User) {
        db.collection("Users").document(user.userId!!).set(user)
    }

    fun likeArtworkForUser(userId: String, artworkId: String) {
        db.collection("Users").document(userId)
            .update("likedArtworks", FieldValue.arrayUnion(artworkId))
    }

    fun unlikeArtworkForUser(userId: String, artworkId: String) {
        db.collection("Users").document(userId)
            .update("likedArtworks", FieldValue.arrayRemove(artworkId))
    }

    fun getUserLikedArtworks(userId: String, callback: (User?) -> Unit) {
        db.collection("Users").document(userId).get().addOnSuccessListener { document ->
            callback(document.toObject(User::class.java))
        }
    }


}
