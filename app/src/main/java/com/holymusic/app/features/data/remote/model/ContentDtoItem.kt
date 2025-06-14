package com.holymusic.app.features.data.remote.model

data class ContentDtoItem(
    val albumId: String? = null,
    val albumName: String? = null,
    val artistId: String? = null,
    val artistName: String? = null,
    val category: String? = null,
    val categoryName: String? = null,
    val contentCategory: String? = null,
    val contentId: String? = null,
    val contentName: String? = null,
    val contentOrder: Int? = null,
    val contentType: String? = null,
    val contentUrl: String? = null,
    val copyright: String? = null,
    val imageUrl: String? = null,
    val labelName: String? = null,
    val subcategory: String? = null,
    val subcategoryName: String? = null,
    val text: String? = null,
    val textInArabic: String? = null
)

fun ContentDtoItem.toAlbum() : AlbumDtoItem {
    return AlbumDtoItem(
        id = albumId,
        imageUrl = imageUrl,
        title = contentName,
        about = labelName
    )
}
