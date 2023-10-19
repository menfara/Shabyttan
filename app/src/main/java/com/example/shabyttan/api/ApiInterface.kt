package com.example.shabyttan.api

import com.example.shabyttan.models.MuseumData
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("api/artworks/?has_image=1&limit=1&skip=3177")
    suspend fun getArtworks() : Response<MuseumData>
}