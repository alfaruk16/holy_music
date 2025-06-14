package com.holymusic.app.features.data.local

import com.holymusic.app.features.data.local.entities.LocalTrack
import com.holymusic.app.features.data.remote.model.TracksDtoItem

fun TracksDtoItem.toLocal() = LocalTrack(
    about = about,
    appLanguage = appLanguage,
    artistId = artistId,
    artistName = artistName,
    category = category,
    categoryName = categoryName,
    contentBaseUrl = contentBaseUrl,
    contentCategory = contentCategory,
    createdBy = createdBy,
    createdOn = createdOn,
    duration = duration,
    genreId = genreId,
    id = id ?: "",
    imageUrl = imageUrl,
    isActive = isActive,
    lyrics = lyrics,
    sequenceNo = sequenceNo,
    streamUrl = streamUrl,
    subcategory = subcategory,
    subcategoryName = subcategoryName,
    title = title,
    updatedBy = updatedBy,
    updatedOn = updatedOn,
    playCount = playCount,
    totalFav = totalFav,
    isPremium = isPremium
)

fun LocalTrack.toExternal() = TracksDtoItem(
    about = about,
    appLanguage = appLanguage,
    artistId = artistId,
    artistName = artistName,
    category = category,
    categoryName = categoryName,
    contentBaseUrl = contentBaseUrl,
    contentCategory = contentCategory,
    createdBy = createdBy,
    createdOn = createdOn,
    duration = duration,
    genreId = genreId,
    id = id,
    imageUrl = imageUrl,
    isActive = isActive,
    lyrics = lyrics,
    sequenceNo = sequenceNo,
    streamUrl = streamUrl,
    subcategory = subcategory,
    subcategoryName = subcategoryName,
    title = title,
    updatedBy = updatedBy,
    updatedOn = updatedOn,
    playCount = playCount,
    totalFav = totalFav,
    isPremium = isPremium
)

@JvmName("localToExternal")
fun List<LocalTrack>.toExternal() = map(LocalTrack::toExternal)

@JvmName("externalToLocal")
fun List<TracksDtoItem>.toLocal() = map(TracksDtoItem::toLocal)
