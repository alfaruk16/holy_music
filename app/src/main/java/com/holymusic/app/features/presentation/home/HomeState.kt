package com.holymusic.app.features.presentation.home

import com.holymusic.app.core.util.Enums
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.data.remote.model.AlbumDtoItem
import com.holymusic.app.features.data.remote.model.ArtistDto
import com.holymusic.app.features.data.remote.model.PromotionsDto
import com.holymusic.app.features.data.remote.model.SubStatusDtoItem
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.data.remote.model.TrackBillboardDtoItem
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.data.remote.model.TracksDtoItem

data class HomeState(
    val mobile: String = "",
    val isLoading: Boolean = true,
    val error: String = "",
    val trackBillboard: TrackBillboardDto = TrackBillboardDto(),
    val artist: ArtistDto = ArtistDto(),
    val tracks: TracksDto = TracksDto(),
    val scholarTracks: TracksDto = TracksDto(),
    val popularTracks: TracksDto = TracksDto(),
    val videoTracks: TracksDto = TracksDto(),
    val popularVideoTracks: TracksDto = TracksDto(),
    val albums: AlbumDto = AlbumDto(),
    val videoAlbums: AlbumDto = AlbumDto(),
    val isLoaderClosed: Boolean = false,
    val favoriteTracks: TracksDto = TracksDto(),
    val favoriteVideoTracks: TracksDto = TracksDto(),
    val shortPlay: TrackBillboardDtoItem = TrackBillboardDtoItem(),
    val trending: List<TrackBillboardDtoItem> = emptyList(),
    val subStatusDtoItem: SubStatusDtoItem = SubStatusDtoItem(),
    val promotions: PromotionsDto = PromotionsDto(),
    val currentTrack: TracksDtoItem = TracksDtoItem(),
    val videoType: String = Enums.VideoType.Artist.name,
    val page: Int = 1,
    val isDark: Int = 0,
    val babarAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65a7feb85252e5a16e6ac9d9",
        imageUrl = "artist/babargojol_audio_<\$size$>.jpg",
        title = "বাবার গজল"
    ),
    val babarTracks: TracksDto = TracksDto(),
    val deshAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65a8f9544441df355874f84e",
        imageUrl = "artist/deshergojol_audio_<\$size$>.jpg",
        title = "দেশাত্মবোধক গজল"
    ),
    val deshTracks: TracksDto = TracksDto(),
    val maAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65ae8de1c0557ced9403c6cc",
        imageUrl = "artist/mayergojol_audio_<\$size$>.jpg",
        title = "মায়ের গজল"
    ),
    val maTracks: TracksDto = TracksDto(),
    val shishuAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65ae8f92c0557ced9403c6d2",
        imageUrl = "artist/shishudergojol_audio_<\$size$>.jpg",
        title = "শিশুদের গজল"
    ),
    val shishuTracks: TracksDto = TracksDto(),
    val jonoprioAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65fbc6928453e6c0155cabc5",
        imageUrl = "artist\\JonoprioSongeet_192.jpg",
        title = "জনপ্রিয় সঙ্গীত"
    ),
    val jonoprioTracks: TracksDto = TracksDto(),
    val selectedAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65fbcaa58453e6c0155cabcd",
        imageUrl = "artist\\SelectedSong_192.jpg",
        title = "সিলেক্টেড"
    ),
    val selectedTracks: TracksDto = TracksDto(),
    val zamanAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65155ef6332fc6591b5a76c8",
        imageUrl = "artist\\MuhammadBadruzzaman_192.jpg",
        title = "বদরুজ্জামান এক্সক্লুসিভ"
    ),
    val zamanTracks: TracksDto = TracksDto(),
    val syedAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65155f44332fc6591b5a76ce",
        imageUrl = "artist\\SayedAhmad_192.jpg",
        title = "সাঈদ আহমেদ এক্সক্লুসিভ"
    ),
    val syedTracks: TracksDto = TracksDto(),
    val rayhanAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "66125a95324eead105c03fcb",
        imageUrl = "artist\\AbuRayhan_Sr_192.jpg",
        title = "আবু রায়হান এক্সক্লুসিভ"
    ),
    val rayhanTracks: TracksDto = TracksDto(),
    val popularVideoAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "65fbc8018453e6c0155cabca",
        imageUrl = "artist\\Popularvideos_400.jpg",
        title = "পপুলার ভিডিও"
    ),
    val popularVideoAlbumTrack: TracksDto = TracksDto(),
    val latestReleaseAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "66e7ff9b817a0716dbf07a22",
        imageUrl = "gojol\\Preview\\Latestrelese_192.jpg",
        title = "লেটেস্ট রিলিজ"
    ),
    val latestReleaseTrack: TracksDto = TracksDto(),
    val latestReleaseVideoAlbum: AlbumDtoItem = AlbumDtoItem(
        contentBaseUrl = "https://v4technologies.sgp1.cdn.digitaloceanspaces.com/muslimbd/",
        id = "66e7fe3e817a0716dbf07a15",
        imageUrl = "gojol\\Preview\\Latestrelese_400.jpg",
        title = "লেটেস্ট রিলিজ"
    ),
    val latestReleaseVideoTrack: TracksDto = TracksDto()
)
