package com.holymusic.app.features.domain.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.local.model.FileItem
import com.holymusic.app.features.data.local.model.Download
import com.holymusic.app.features.domain.repository.DownloadRepo
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.concurrent.thread


class AndroidDownloader @Inject constructor(
    private val application: Application,
    private val trackRepo: DownloadRepo
) : AndroidViewModel(application) {

    private val downloadManager = application.getSystemService(DownloadManager::class.java)
    private val sharedPreferences =
        application.getSharedPreferences(LocalConstant.sharedPreferences, Context.MODE_PRIVATE)

    init {
        subscribeEventBus()
    }

    private fun subscribeEventBus() {
        viewModelScope.launch {
            EventBus.subscribe<String> { event ->
                if (event == AppConstants.local) {
                    val file = sharedPreferences.getString(LocalConstant.local, "")
                    if (!file.isNullOrEmpty()) {
                        addToLocalDatabase(Gson().fromJson(file, FileItem::class.java))
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.remove(LocalConstant.local)
                        editor.apply()
                    }
                }
            }
        }
    }

    private fun addToLocalDatabase(file: FileItem) {
        viewModelScope.launch {
            trackRepo.createTrack(
                Download(
                    id = file.id,
                    title = file.name,
                    artistName = file.artistName,
                    description = file.description,
                    duration = file.duration,
                    percentage = 100,
                    isComplete = true,
                    contentBaseUrl = file.contentBaseUrl,
                    imageUrl = file.imageUrl,
                    localPath = file.url,
                    contentType = file.mimeType
                )
            )
        }
    }

    @SuppressLint("Range")
    fun downloadFile(
        activity: Activity,
        file: FileItem,
        progress: (Int) -> Unit
    ): Long {

        var percentage = 0
        val url = file.url.replace("\\", "/").toUri()
        var downloadId: Long = -1L

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            .toString() + AppConstants.directory + file.name + file.mimeType

        val targetFile = File(path)

        if (!targetFile.exists()) {

            if (checkStoragePermission(activity)) {

                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(application, "Download Started", Toast.LENGTH_SHORT).show()
                }

                val request = DownloadManager.Request(url)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_MUSIC,
                        AppConstants.directory + file.name + file.mimeType
                    ).setDescription(Gson().toJson(file.copy(url = path)))

                downloadId = downloadManager.enqueue(request)

                thread {
                    var downloading = true
                    while (downloading) {
                        val query = DownloadManager.Query()
                        query.setFilterById(downloadId)

                        val cursor = downloadManager.query(query)
                        if (cursor.moveToFirst()) {
                            val bytesDownloaded =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            val bytesTotal =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                            val progressPercentage = ((bytesDownloaded * 100L) / bytesTotal).toInt()

                            if (percentage != progressPercentage) {
                                println(progressPercentage)
                                percentage = progressPercentage
                                progress(progressPercentage)
                            }

                            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                downloading = false
                            }
                            cursor.close()
                        }
                    }
                }
            }
        } else {
            println("already downloaded")
        }
        return downloadId
    }

    override fun onCleared() {
        downloadManager.remove()
        super.onCleared()
    }

    suspend fun isDownloaded(id: String?): Boolean {
        return trackRepo.isDownloaded(id ?: "")
    }
}

fun checkStoragePermission(activity: Activity): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_MEDIA_AUDIO
        )
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_MEDIA_VIDEO
        )
                != PackageManager.PERMISSION_GRANTED)
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_VIDEO),
            202
        )
        return false
    } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
                != PackageManager.PERMISSION_GRANTED)
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            201
        )
        return false
    }
    return true
}

