package edu.metrostate.ics342.mediatracker.data.network

import edu.metrostate.ics342.mediatracker.data.model.Favorite
import edu.metrostate.ics342.mediatracker.data.model.LibraryItem
import edu.metrostate.ics342.mediatracker.data.model.Media
import edu.metrostate.ics342.mediatracker.data.model.Review
import edu.metrostate.ics342.mediatracker.data.model.Priority
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@kotlinx.serialization.Serializable
data class AddToLibraryRequest(val mediaId: Int, val status: String)

@kotlinx.serialization.Serializable
data class AddFavoriteRequest(val mediaId: Int)

@kotlinx.serialization.Serializable
data class UpdateLibraryStatusRequest(val status: String)

@kotlinx.serialization.Serializable
data class PriorityWriteItem(
    val mediaId: Int,
    val priority: Int,
    val orderIndex: Int,
    val estimatedTimeHours: Double? = null,
    val notes: String? = null
)

interface MediaApiService {
    @GET("media")
    suspend fun searchMedia(
        @Query("query") query: String? = null,
        @Query("type") type: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("after") after: String? = null
    ): Response<List<Media>>

    @GET("media/{id}")
    suspend fun getMedia(@Path("id") id: Int): Response<Media>

    @GET("library/{mediaId}")
    suspend fun getLibraryStatus(@Path("mediaId") mediaId: Int): Response<LibraryItem>

    @GET("library")
    suspend fun getLibrary(@Query("status") status: String? = null): Response<List<LibraryItem>>

    @POST("library")
    suspend fun addToLibrary(@Body request: AddToLibraryRequest): Response<LibraryItem>

    @PUT("library/{mediaId}")
    suspend fun updateLibraryStatus(
        @Path("mediaId") mediaId: Int,
        @Body request: UpdateLibraryStatusRequest
    ): Response<LibraryItem>

    @DELETE("library/{mediaId}")
    suspend fun removeFromLibrary(@Path("mediaId") mediaId: Int): Response<Unit>

    @GET("favorites/{mediaId}")
    suspend fun getFavoriteStatus(@Path("mediaId") mediaId: Int): Response<Favorite>

    @POST("favorites")
    suspend fun addFavorite(@Body request: AddFavoriteRequest): Response<Favorite>

    @DELETE("favorites/{mediaId}")
    suspend fun removeFavorite(@Path("mediaId") mediaId: Int): Response<Unit>

    @GET("reviews")
    suspend fun getReviews(@Query("mediaId") mediaId: Int): Response<List<Review>>

    @GET("priorities")
    suspend fun getPriorities(): Response<List<Priority>>

    @PUT("priorities")
    suspend fun updatePriorities(@Body request: PriorityWriteItem): Response<Priority>
}