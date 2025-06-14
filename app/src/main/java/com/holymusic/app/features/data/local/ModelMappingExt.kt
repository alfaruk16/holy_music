package com.holymusic.app.features.data.local

import com.holymusic.app.features.data.local.entities.LocalDownload
import com.holymusic.app.features.data.local.model.Download
import com.holymusic.app.features.data.remote.model.TracksDtoItem

fun Download.toLocal() = LocalDownload(
    id = id,
    title = title,
    artistName = artistName,
    description = description,
    localPath = localPath,
    contentBaseUrl = contentBaseUrl,
    imageUrl = imageUrl,
    duration = duration,
    percentage = percentage,
    isComplete = isComplete,
    contentType = contentType
)

fun LocalDownload.toExternal() = Download(
    id = id,
    title = title,
    artistName = artistName,
    description = description,
    localPath = localPath,
    contentBaseUrl = contentBaseUrl,
    imageUrl = imageUrl,
    duration = duration,
    percentage = percentage,
    isComplete = isComplete,
    contentType = contentType
)

@JvmName("localToExternal")
fun List<LocalDownload>.toExternal() = map(LocalDownload::toExternal)

fun Download.toTrackDtoItem() = TracksDtoItem(
    id = id,
    contentBaseUrl = contentBaseUrl,
    imageUrl = imageUrl,
    streamUrl = localPath,
    title = title,
    artistName = artistName,
    duration = duration,
    about = description,
    contentCategory = contentType
)
