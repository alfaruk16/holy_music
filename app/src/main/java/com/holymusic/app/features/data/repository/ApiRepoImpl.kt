package com.holymusic.app.features.data.repository

import com.holymusic.app.features.data.remote.Apis
import com.holymusic.app.features.data.remote.entity.Favorite
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
import com.holymusic.app.features.domain.repository.ApiRepo
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

class ApiRepoImpl @Inject constructor(
    private val api: Apis
) : ApiRepo {

    override suspend fun getCategories(): CategoryDto {
        return api.getCategories()
    }

    override suspend fun getContents(): ContentsDto {
        return api.getContents()
    }

    override suspend fun getAlQuran(): AlQuranDto {
        return api.getAlQuran()
    }

    override suspend fun getCategoryContents(id: String): CategoryContentsDto {
        return api.getCategoryContents(id = id)
    }

    override suspend fun getSubCategoryTextContents(id: String): CategoryContentsDto {
        return api.getSubCategoryContents(id = id)
    }

    override suspend fun getSubCategories(id: String): SubCategoriesDto {
        return api.getSubCategories(id = id)
    }


    override suspend fun getSurah(id: String): SurahDto {
        return api.getSurah(id = id)
    }

    override suspend fun getArtist(): ArtistDto {
        return api.getArtist()
    }

    override suspend fun getScholar(): ArtistDto {
        return api.getScholar()
    }

    override suspend fun getAllahNames(): AllahNamesDto {
        return api.getAllahNames()
    }

    override suspend fun getTracksByType(type: String, skip: String, take: String): TracksDto {
        return api.getTracksByType(type = type, skip = skip, take = take)
    }

    override suspend fun getScholarTracksByType(type: String): TracksDto {
        return api.getScholarTracksByType(type = type)
    }

    override suspend fun getTracksByArtist(artistId: String, type: String): TracksDto {
        return api.getTracksByArtist(artistId = artistId, type = type)
    }

    override suspend fun getScholarTracksByArtist(artistId: String, type: String): TracksDto {
        return api.getScholarTracksByArtist(artistId = artistId, type = type)
    }

    override suspend fun getAlbumByType(type: String): AlbumDto {
        return api.getAlbumByType(type = type)
    }

    override suspend fun getAlbumTrack(id: String): AlbumTrackDto {
        return api.getAlbumTrack(id = id)
    }

    override suspend fun getVideoTracks(id: String): TracksDto {
        return api.getVideoTracks(body = Track(artId = id))
    }

    override suspend fun getHomeSurah(): AlQuranDto {
        return api.getHomeSurah(body = User())
    }

    override suspend fun getHomeAllahNames(): AllahNamesDto {
        return api.getHomeAllahNames(body = User())
    }

    override suspend fun getPrayerTimes(date: String, month: String): PrayerTimesDto {
        return api.getPrayerTimes(date = date, month = month)
    }

    override suspend fun getTextContent(id: String): TextContentDto {
        return api.getTextContent(id = id)
    }

    override suspend fun getImageContents(id: String): ImageContentsDto {
        return api.getImageContents(id = id)
    }

    override suspend fun signUp(body: SignUp): SignUpDto {
        return api.signUp(body)
    }

    override suspend fun updateProfile(body: RequestBody): ResponseBody {
        return api.updateProfile(body = body)
    }

    override suspend fun login(body: Login): LoginDto {
        return api.login(body)
    }

    override suspend fun getTrackBillboard(): TrackBillboardDto {
        return api.getTrackBillboard()
    }

    override suspend fun getTrack(id: String): TrackDto {
        return api.getTrack(id = id)
    }

    override suspend fun addPlayCount(playCount: RequestBody): ResponseBody {
        return api.addPlayCount(request = playCount)
    }

    override suspend fun addPlayCountScholar(playCount: RequestBody): ResponseBody {
        return  api.addPlayCountScholar(request = playCount)
    }

    override suspend fun resetPassword(userName: String, password: String): ResponseBody {
        return  api.resetPassword(userName = userName, password = password)
    }

    override suspend fun getProfile(): ProfileDto {
        return api.getProfile()
    }

    override suspend fun setFavorite(favorite: Favorite): DefaultDto {
        return api.setFavorite(trackId = favorite.trackId, artistId = favorite.albumId)
    }

    override suspend fun isFavorite(trackId: String): DefaultDto {
        return api.isFavorite(trackId = trackId)
    }

    override suspend fun cancelFavorite(trackId: String): DefaultDto {
        return api.cancelFavorite(trackId = trackId)
    }

    override suspend fun getMyFavorites(): TracksDto {
        return api.getMyFavorites()
    }

    override suspend fun getArtistById(id: String): SingleArtistDto {
        return api.getArtistById(id = id)
    }

    override suspend fun getAlbumById(id: String): SingleAlbumDto {
        return api.getAlbumById(id = id)
    }

    override suspend fun getKhatameQuran(): KhatameQuranDto {
        return api.getKhatameQuran()
    }

    override suspend fun getScholarById(id: String): ScholarDto {
        return api.getScholarById(id = id)
    }

    override suspend fun getScholarTrackById(id: String): ScholarTrackDto {
        return api.getScholarTrackById(id = id)
    }

    override suspend fun getSingleKhatam(id: String): SingleKhatamDto {
        return api.getKhatameQuranItem(id = id)
    }

    override suspend fun getPromotions(): PromotionsDto {
        return api.getPromotions()
    }
}