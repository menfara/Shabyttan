package com.example.shabyttan.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shabyttan.api.ApiInterface
import com.example.shabyttan.models.MuseumData

class ArtworksRepository(private val apiInterface: ApiInterface) {

    private val artworksLiveData = MutableLiveData<MuseumData>()

    val artworks : LiveData<MuseumData>
        get() = artworksLiveData

    suspend fun getArtworks() {
        try {
            val response = apiInterface.getArtworks()
            if (response.isSuccessful) {
                artworksLiveData.postValue(response.body())
            } else {
                Log.e("MyTag", "Request failed with code ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("MyTag", "Error: ${e.message}", e)
        }
    }
}