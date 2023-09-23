package com.example.shabyttan.api

import com.example.shabyttan.models.MuseumData
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("api/artworks/?limit=1&skip=5")
    suspend fun getArtworks() : Response<MuseumData>
}