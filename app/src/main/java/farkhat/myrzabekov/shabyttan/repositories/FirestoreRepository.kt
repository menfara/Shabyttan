package farkhat.myrzabekov.shabyttan.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getArtworkDataById(documentId: String, callback: (Artwork?) -> Unit) {
        Log.d("MyTag", "Fetching artwork data by ID: $documentId...")

        db.collection("artworks")
            .document(documentId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                Log.d("MyTag", "Successfully fetched data.")

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("MyTag", "Document data: ${documentSnapshot.toString()}")

                    val artwork = Artwork(
                        author = documentSnapshot.getString("author"),
                        description = documentSnapshot.getString("description"),
                        funFact = documentSnapshot.getString("fun_fact"),
                        imageUrl = documentSnapshot.getString("image_url"),
                        title = documentSnapshot.getString("title")
                    )
                    callback(artwork)
                } else {
                    Log.d("MyTag", "No data found.")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MyTag", "Error fetching data: ${exception.message}", exception)
                callback(null)
            }
    }

    data class Artwork(
        val title: String?,
        val author: String?,
        val description: String?,
        val funFact: String?,
        val imageUrl: String?
    )
}
