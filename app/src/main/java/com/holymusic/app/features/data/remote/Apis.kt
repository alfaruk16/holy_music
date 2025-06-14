package com.holymusic.app.features.data.remote

import com.holymusic.app.MainActivity
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.remote.entity.Login
import com.holymusic.app.features.data.remote.entity.SignUp
import com.holymusic.app.features.data.remote.entity.Track
import com.holymusic.app.features.data.remote.entity.User
import com.holymusic.app.features.data.remote.model.AlQuranDto
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumTrackDto
import com.holymusic.app.features.data.remote.model.AllahNamesDto
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.CategoryContentsDto
import com.holymusic.app.features.data.remote.model.CategoryDto
import com.holymusic.app.features.data.remote.model.ContentsDto
import com.holymusic.app.features.data.remote.model.DefaultDto
import com.holymusic.app.features.data.remote.model.ImageContentsDto
import com.holymusic.app.features.data.remote.model.KhatameQuranDto
import com.holymusic.app.features.data.remote.model.LoginDto
import com.holymusic.app.features.data.remote.model.PrayerTimesDto
import com.holymusic.app.features.data.remote.model.ProfileDto
import com.holymusic.app.features.data.remote.model.PromotionsDto
import com.holymusic.app.features.data.remote.model.ScholarDto
import com.holymusic.app.features.data.remote.model.ScholarTrackDto
import com.holymusic.app.features.data.remote.model.SignUpDto
import com.holymusic.app.features.data.remote.model.SingleAlbumDto
import com.holymusic.app.features.data.remote.model.SingleArtistDto
import com.holymusic.app.features.data.remote.model.SingleKhatamDto
import com.holymusic.app.features.data.remote.model.SubCategoriesDto
import com.holymusic.app.features.data.remote.model.SurahDto
import com.holymusic.app.features.data.remote.model.TextContentDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.data.remote.model.TrackDto
import com.holymusic.app.features.data.remote.model.TracksDto
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface Apis {

    @Headers("Content-Type: application/json")
    @GET("Category/${AppConstants.language}/1/0/${AppConstants.search}")
    suspend fun getCategories(@Header("Authorization") token: String = MainActivity.token): CategoryDto

    @Headers("Content-Type: application/json")
    @GET("Publish/publishedcontent/${AppConstants.language}")
    suspend fun getContents(@Header("Authorization") token: String = MainActivity.token): ContentsDto

    @Headers("Content-Type: application/json")
    @GET("Surah/${AppConstants.language}/1/0/${AppConstants.search}")
    suspend fun getAlQuran(@Header("Authorization") token: String = MainActivity.token): AlQuranDto

    @Headers("Content-Type: application/json")
    @GET("TextContent/bycategory/{id}/${AppConstants.search}/1/0")
    suspend fun getCategoryContents(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): CategoryContentsDto

    @Headers("Content-Type: application/json")
    @GET("TextContent/bysubcategory/{id}/1/0")
    suspend fun getSubCategoryContents(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): CategoryContentsDto

    @Headers("Content-Type: application/json")
    @GET("Subcategory/bycategory/{id}/1/0")
    suspend fun getSubCategories(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): SubCategoriesDto

    @Headers("Content-Type: application/json")
    @GET("Ayat/bysurah/{id}/1/0")
    suspend fun getSurah(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): SurahDto

    @Headers("Content-Type: application/json")
    @GET("Artist/${AppConstants.language}/1/0/undefined")
    suspend fun getArtist(@Header("Authorization") token: String = MainActivity.token): ArtistDto

    @Headers("Content-Type: application/json")
    @GET("Scholar/${AppConstants.language}/1/0/undefined")
    suspend fun getScholar(@Header("Authorization") token: String = MainActivity.token): ArtistDto

    @Headers("Content-Type: application/json")
    @GET("NinetyNineName/${AppConstants.language}/1/0?searchText=${AppConstants.search}")
    suspend fun getAllahNames(@Header("Authorization") token: String = MainActivity.token): AllahNamesDto

    @Headers("Content-Type: application/json")
    @GET("Track/bycontenttype/${AppConstants.language}/{type}/{skip}/{take}/${AppConstants.search}")
    suspend fun getTracksByType(
        @Header("Authorization") token: String = MainActivity.token.ifEmpty { LocalConstant.freeToken },
        @Path("type") type: String,
        @Path("skip") skip: String,
        @Path("take") take: String
    ): TracksDto

    @Headers("Content-Type: application/json")
    @GET("ScholarTrack/bycontenttype/${AppConstants.language}/{type}/1/0/${AppConstants.search}")
    suspend fun getScholarTracksByType(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("type") type: String
    ): TracksDto

    @Headers("Content-Type: application/json")
    @GET("Track/byartist/${AppConstants.language}/{type}/{artistId}/1/0/${AppConstants.search}")
    suspend fun getTracksByArtist(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("artistId") artistId: String,
        @Path("type") type: String
    ): TracksDto

    @Headers("Content-Type: application/json")
    @GET("ScholarTrack/byartist/${AppConstants.language}/{type}/{artistId}/1/0/${AppConstants.search}")
    suspend fun getScholarTracksByArtist(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("artistId") artistId: String,
        @Path("type") type: String
    ): TracksDto

    @Headers("Content-Type: application/json")
    @GET("TrackAlbum/bycontenttype/${AppConstants.language}/{type}/1/0/${AppConstants.search}")
    suspend fun getAlbumByType(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("type") type: String
    ): AlbumDto

    @Headers("Content-Type: application/json")
    @GET("AlbumContent/byalbum/{id}/1/0/${AppConstants.search}")
    suspend fun getAlbumTrack(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): AlbumTrackDto

    @Headers("Content-Type: application/json")
    @POST("artisttrackvideo")
    suspend fun getVideoTracks(
        @Header("Authorization") token: String = MainActivity.token,
        @Body body: Track
    ): TracksDto

    @Headers("Content-Type: application/json")
    @POST("quransuraonselect")
    suspend fun getHomeSurah(
        @Header("Authorization") token: String = MainActivity.token,
        @Body body: User
    ): AlQuranDto

    @Headers("Content-Type: application/json")
    @POST("allahnamesonselect")
    suspend fun getHomeAllahNames(
        @Header("Authorization") token: String = MainActivity.token,
        @Body body: User
    ): AllahNamesDto

    @Headers("Content-Type: application/json")
    @GET("Prayer/GetPrayerTime/{date}-{month}")
    suspend fun getPrayerTimes(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("date") date: String,
        @Path("month") month: String
    ): PrayerTimesDto

    @Headers("Content-Type: application/json")
    @GET("TextContent/{id}")
    suspend fun getTextContent(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): TextContentDto

    @Headers("Content-Type: application/json")
    @GET("ImageContent/bycategory/{id}/undefined/1/0")
    suspend fun getImageContents(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): ImageContentsDto

    @Headers("Content-Type: application/json")
    @POST("account/registration")
    suspend fun signUp(@Body body: SignUp): SignUpDto

    @Headers("Content-Type: application/json")
    @POST("account/login")
    suspend fun login(@Body body: Login): LoginDto

    @Headers("Content-Type: application/json")
    @GET("TrackBillboard/${AppConstants.language}/1/0/${AppConstants.search}/ht")
    suspend fun getTrackBillboard(
        @Header("Authorization") token: String = MainActivity.token
    ): TrackBillboardDto

    @Headers("Content-Type: application/json")
    @GET("Track/{id}")
    suspend fun getTrack(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): TrackDto

    @Multipart
    @POST("Track/AddPlaycount")
    suspend fun addPlayCount(
        @Header("Authorization") token: String = MainActivity.token,
        @Part("payload") request: RequestBody
    ): ResponseBody

    @Multipart
    @POST("ScholarTrack/AddPlaycount")
    suspend fun addPlayCountScholar(
        @Header("Authorization") token: String = MainActivity.token,
        @Part("payload") request: RequestBody
    ): ResponseBody

    @Headers("Content-Type: application/json")
    @POST("account/forgetpassword")
    suspend fun resetPassword(
        @Query("userName") userName: String,
        @Query("password") password: String,
        @Query("AppTypes") appTypes: String = "ht"
    ): ResponseBody

    @Multipart
    @PUT("Account/updateprofile")
    suspend fun updateProfile(
        @Header("Authorization") token: String = MainActivity.token,
        @Part("payload") body: RequestBody
    ): ResponseBody

    @Headers("Content-Type: application/json")
    @GET("Account/getprofile")
    suspend fun getProfile(@Header("Authorization") token: String = MainActivity.token): ProfileDto

    @Headers("Content-Type: application/json")
    @POST("Track/Favourite/${AppConstants.language}/{trackId}/{artistId}/ht")
    suspend fun setFavorite(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("trackId") trackId: String,
        @Path("artistId") artistId: String
    ): DefaultDto

    @Headers("Content-Type: application/json")
    @GET("Track/isFavourite/{trackId}")
    suspend fun isFavorite(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("trackId") trackId: String
    ): DefaultDto

    @Headers("Content-Type: application/json")
    @DELETE("Track/unFavourite/{trackId}")
    suspend fun cancelFavorite(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("trackId") trackId: String
    ): DefaultDto

    @Headers("Content-Type: application/json")
    @GET("Track/Favourite/${AppConstants.language}/1/0")
    suspend fun getMyFavorites(@Header("Authorization") token: String = MainActivity.token): TracksDto

    @Headers("Content-Type: application/json")
    @GET("Artist/{id}")
    suspend fun getArtistById(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): SingleArtistDto

    @Headers("Content-Type: application/json")
    @GET("TrackAlbum/{id}")
    suspend fun getAlbumById(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): SingleAlbumDto

    @Headers("Content-Type: application/json")
    @GET("VideoContent/bycategory/65d4c38a83538eb52c7dccc4/${AppConstants.search}/1/0")
    suspend fun getKhatameQuran(@Header("Authorization") token: String = MainActivity.token.ifEmpty { LocalConstant.freeToken }): KhatameQuranDto

    @Headers("Content-Type: application/json")
    @GET("Scholar/{id}")
    suspend fun getScholarById(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): ScholarDto

    @Headers("Content-Type: application/json")
    @GET("ScholarTrack/{id}")
    suspend fun getScholarTrackById(
        @Header("Authorization") token: String = MainActivity.token,
        @Path("id") id: String
    ): ScholarTrackDto

    @Headers("Content-Type: application/json")
    @GET("VideoContent/{id}")
    suspend fun getKhatameQuranItem(
        @Header("Authorization") token: String = MainActivity.token.ifEmpty { LocalConstant.freeToken },
        @Path("id") id: String
    ): SingleKhatamDto

    @Headers("Content-Type: application/json")
    @GET("Promotion/GetActiveAll/bn/ht/1/0")
    suspend fun getPromotions(
        @Header("Authorization") token: String = MainActivity.token.ifEmpty { LocalConstant.freeToken }
    ): PromotionsDto

}
