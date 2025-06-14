package com.holymusic.app.features.domain.repository

import com.holymusic.app.features.data.remote.entity.Favorite
import com.holymusic.app.features.data.remote.entity.Login
import com.holymusic.app.features.data.remote.entity.SignUp
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

interface ApiRepo {

    suspend fun getCategories() : CategoryDto

    suspend fun getContents() : ContentsDto

    suspend fun getAlQuran(): AlQuranDto

    suspend fun getCategoryContents(id: String): CategoryContentsDto

    suspend fun getSubCategoryTextContents(id: String): CategoryContentsDto

    suspend fun getSubCategories(id: String): SubCategoriesDto

    suspend fun getSurah(id: String): SurahDto

    suspend fun getArtist(): ArtistDto

    suspend fun getScholar(): ArtistDto

    suspend fun getAllahNames(): AllahNamesDto

    suspend fun getTracksByType(type: String, skip: String = "1", take: String = "0"): TracksDto

    suspend fun getScholarTracksByType(type: String): TracksDto

    suspend fun getTracksByArtist(artistId: String, type: String): TracksDto

    suspend fun getScholarTracksByArtist(artistId: String, type: String): TracksDto

    suspend fun getAlbumByType(type: String): AlbumDto

    suspend fun getAlbumTrack(id: String): AlbumTrackDto

    suspend fun getVideoTracks(id: String): TracksDto

    suspend fun getHomeSurah(): AlQuranDto

    suspend fun getHomeAllahNames(): AllahNamesDto

    suspend fun getPrayerTimes(date: String, month: String): PrayerTimesDto

    suspend fun getTextContent(id: String): TextContentDto

    suspend fun getImageContents(id: String): ImageContentsDto

    suspend fun signUp(body: SignUp): SignUpDto

    suspend fun updateProfile(body: RequestBody): ResponseBody

    suspend fun login(body: Login): LoginDto

    suspend fun getTrackBillboard(): TrackBillboardDto

    suspend fun getTrack(id: String): TrackDto

    suspend fun addPlayCount(playCount: RequestBody): ResponseBody

    suspend fun addPlayCountScholar(playCount: RequestBody): ResponseBody

    suspend fun resetPassword(userName: String, password: String): ResponseBody

    suspend fun getProfile(): ProfileDto

    suspend fun setFavorite(favorite: Favorite): DefaultDto

    suspend fun isFavorite(trackId: String): DefaultDto

    suspend fun cancelFavorite(trackId: String): DefaultDto

    suspend fun getMyFavorites(): TracksDto

    suspend fun getArtistById(id: String): SingleArtistDto
    suspend fun getAlbumById(id: String): SingleAlbumDto

    suspend fun getKhatameQuran(): KhatameQuranDto
    suspend fun getScholarById(id: String): ScholarDto
    suspend fun getScholarTrackById(id: String): ScholarTrackDto
    suspend fun getSingleKhatam(id: String): SingleKhatamDto
    suspend fun getPromotions(): PromotionsDto


}