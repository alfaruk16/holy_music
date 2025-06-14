package com.holymusic.app

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.holymusic.app.core.util.AppConstants.deepLinkId
import com.holymusic.app.core.util.AppConstants.deepLinkScreen
import com.holymusic.app.core.util.LocalConstant
import com.holymusic.app.core.util.LocalConstant.freeToken
import com.holymusic.app.features.presentation.NavGraph
import com.holymusic.app.features.presentation.Screens
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import com.holymusic.app.core.theme.HolyMusicTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        var token = freeToken
        val isPremium = mutableStateOf(false)
        var isLoggedIn = false
        var isDark = mutableStateOf(false)
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = this.getSharedPreferences(LocalConstant.sharedPreferences, MODE_PRIVATE)
        val bundle: Bundle? = intent.extras
        var deepLink = bundle?.getString(deepLinkScreen) ?: ""
        var deepLinkId = bundle?.getString(deepLinkId) ?: ""

        if (intent.data != null) {
            val url = intent.data.toString().split("?")
            if (url.size > 1) {
                val attributes = url[1].split("&")
                if (attributes.isNotEmpty()) {
                    deepLink = attributes[0]
                }
                if (attributes.size > 1) {
                    deepLinkId = attributes[1]
                }
            }
        }

        checkAuth()

        setContent {
            isDark.value = if (sharedPreferences.getInt(
                    LocalConstant.isDark,
                    0
                ) == 2
            ) true else if (sharedPreferences.getInt(
                    LocalConstant.isDark,
                    0
                ) == 1
            ) false else isSystemInDarkTheme()

            HolyMusicTheme(isDark.value) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        startDestination = Screens.MAIN_SCREEN,
                        deepLink = deepLink,
                        deepLinkId = deepLinkId
                    )
                }
            }
        }
        review()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        //printHashKey(this)
    }

//        private fun printHashKey(pContext: Context) {
//        try {
//            val info = pContext.packageManager.getPackageInfo(
//                pContext.packageName,
//                PackageManager.GET_SIGNATURES
//            )
//            for (signature in info.signatures) {
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                val hashKey = String(Base64.encode(md.digest(), 0))
//                println("printHashKey() Hash Key: $hashKey")
//            }
//        } catch (e: NoSuchAlgorithmException) {
//            println("printHashKey()$e")
//        } catch (e: Exception) {
//           println("printHashKey()$e")
//        }
//    }

    private fun review() {
        if (isLoggedIn) {
            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    manager.launchReviewFlow(this, reviewInfo)
                } else {
                    // There was some problem, log or handle the error code.
                    //@ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode
                    //println(reviewErrorCode.toString())

                }
            }
        }
    }

    private fun checkAuth() {
        val auth = sharedPreferences.getString(LocalConstant.token, "")
        if (!auth.isNullOrEmpty()) {
            isLoggedIn = true
            token = "Bearer $auth"
        } else {
            token = freeToken
        }
    }

    override fun onResume() {
        super.onResume()
        checkInAppUpdate()
    }

    private fun checkInAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _: ActivityResult ->
            run {
                // handle callback
//        if (resultCode != RESULT_OK) {
//            log("Update flow failed! Result code: " + resultCode);
//            // If the update is cancelled or fails,
//            // you can request to start the update again.
//        }
            }
        }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HolyMusicTheme {
        Greeting("Android")
    }
}