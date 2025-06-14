package com.holymusic.app.features.data.remote.model

data class KhatameQuranDtoData(
    val about: String? = null,
    val age: String? = null,
    val appLanguage: String? = null,
    val bannerURL: String? = null,
    val category: String? = null,
    val categoryName: String? = null,
    val composer: Any? = null,
    val contenTtitle: String? = null,
    val contentBaseUrl: String? = null,
    val createdBy: String? = null,
    val createdOn: String? = null,
    val description: String? = null,
    val director: Any? = null,
    val duration: String? = null,
    val ep: Any? = null,
    val epNumber: Any? = null,
    val filePath: String? = null,
    val filePathAudio: String? = null,
    val genreAs: Any? = null,
    val genreCode: Any? = null,
    val id: String? = null,
    val isActive: Boolean? = null,
    val isLove: Any? = null,
    val isWish: Any? = null,
    val labelCode: Any? = null,
    val lyricist: Any? = null,
    val playPercent: Int? = null,
    val previewURL: String? = null,
    val publishingDate: Any? = null,
    val publishingYear: Any? = null,
    val rating: Any? = null,
    val sequenceNo: Int? = null,
    val singer: Any? = null,
    val starring: Any? = null,
    val subTitle: Any? = null,
    val subcategory: Any? = null,
    val subcategoryName: Any? = null,
    val type: Any? = null,
    val updatedBy: Any? = null,
    val updatedOn: Any? = null,
    val viewCount: Int? = null
)

fun KhatameQuranDtoData.toTrackDtoItem(): TracksDtoItem {
    return TracksDtoItem(
        id = id, about = about,
        contentBaseUrl = contentBaseUrl,
        imageUrl = previewURL,
        streamUrl = filePathAudio,
        artistName = categoryName,
        title = contenTtitle,
        duration = duration
    )
}

fun KhatameQuranDtoData.toVideoTrackDtoItem(): TracksDtoItem {
    return TracksDtoItem(
        id = id, about = about,
        contentBaseUrl = contentBaseUrl,
        imageUrl = bannerURL,
        streamUrl = filePath,
        artistName = categoryName,
        title = contenTtitle,
        duration = duration
    )
}