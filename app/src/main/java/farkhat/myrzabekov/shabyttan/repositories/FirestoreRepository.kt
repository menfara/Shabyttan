package farkhat.myrzabekov.shabyttan.repositories

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import farkhat.myrzabekov.shabyttan.models.Artwork
import farkhat.myrzabekov.shabyttan.models.Data

class FirestoreRepository {

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
                    callback(null)
                }
            }
    }


    fun incrementArtworkLikes(documentId: String) {
        db.collection("Artworks").document(documentId).update("likesCount", FieldValue.increment(1))
    }

    fun deleteArtworkById(documentId: String) {
        db.collection("Artworks").document(documentId).delete()
    }

    // USERS COLLECTION METHODS

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
