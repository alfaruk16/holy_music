package com.holymusic.app.features.domain.services

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver
import com.google.gson.Gson
import com.holymusic.app.core.util.AppConstants
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.features.data.local.model.FileItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@SuppressLint("RestrictedApi")
class DownloadCompletedReceiver : BroadcastReceiver() {
    private lateinit var sharedPreferences: SharedPreferences
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent?) {

        sharedPreferences = context.getSharedPreferences(
            LocalConstant.sharedPreferences,
            Context.MODE_PRIVATE
        )


        if (intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if (id != -1L) {
                println("Download with ID $id finished!")

                val manager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                val query = DownloadManager.Query()
                query.setFilterById(id)

                val cursor = manager.query(query)

                if (cursor.moveToFirst()) {
                    val description: String =
                        cursor.getString(
                            cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION) ?: 0
                        )

                    val file = Gson().fromJson(description, FileItem::class.java)

                    val targetFile = File(file.url)

                    if (targetFile.exists()) {
                        val soundDataFile =
                            File.createTempFile("muslim_bd.", ".${file.name}")
                        val fos = FileOutputStream(soundDataFile)
                        fos.write(targetFile.readBytes())
                        fos.close()
                        targetFile.delete()

                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString(LocalConstant.local, Gson().toJson(file.copy(url = soundDataFile.absolutePath)))
                        editor.apply()

                        serviceScope.launch {
                            EventBus.publish(AppConstants.local)
                        }

                    } else {
                        println("file not found")
                    }
                }
                cursor.close()
            }
        }
    }
}