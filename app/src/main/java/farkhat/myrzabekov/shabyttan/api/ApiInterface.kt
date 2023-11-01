package farkhat.myrzabekov.shabyttan.api

import farkhat.myrzabekov.shabyttan.models.MuseumData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    //    @GET("api/artworks/?has_image=1&limit=1&skip=3177")
    @GET("api/artworks/?q=Claude%20Monet&skip=0&limit=1")
    suspend fun getArtworks(): Response<MuseumData>

    @GET("api/artworks/")
    suspend fun getArtworkBySkip(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int = 1
    ): Response<MuseumData>

}