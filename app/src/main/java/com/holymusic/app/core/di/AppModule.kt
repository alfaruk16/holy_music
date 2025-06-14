package com.holymusic.app.core.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.holymusic.app.R
import com.holymusic.app.core.exoplayer.MusicServiceConnection
import com.holymusic.app.features.data.local.DownloadDao
import com.holymusic.app.features.data.local.TrackDao
import com.holymusic.app.features.data.remote.Apis
import com.holymusic.app.features.data.remote.NagadApis
import com.holymusic.app.features.data.remote.PaymentApis
import com.holymusic.app.features.data.remote.SmsApis
import com.holymusic.app.features.data.repository.ApiRepoImpl
import com.holymusic.app.features.data.repository.DownloadRepoImpl
import com.holymusic.app.features.data.repository.NagadPayRepoImpl
import com.holymusic.app.features.data.repository.PaymentApiRepoImpl
import com.holymusic.app.features.data.repository.SmsApiRepoImpl
import com.holymusic.app.features.data.repository.TrackRepoImpl
import com.holymusic.app.features.domain.repository.ApiRepo
import com.holymusic.app.features.domain.repository.DownloadRepo
import com.holymusic.app.features.domain.repository.NagadRepo
import com.holymusic.app.features.domain.repository.PaymentApiRepo
import com.holymusic.app.features.domain.repository.SmsApiRepo
import com.holymusic.app.features.domain.repository.TrackRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api"
    private const val SMS_BASE_URL = "https://api"
    private const val PAYMENT_BASE_URL = "https://api"
    private const val NAGAD_BASE_URL = "https://api"

    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context
    ) = MusicServiceConnection(
        context,
        provideApiRepo(provideApi()),
        provideDownloadRepo(
            DatabaseModule.provideDownloadDao(
                database = DatabaseModule.provideDatabase(context)
            )
        )
    )

    @Provides
    @Singleton
    fun provideApi(): Apis {

        val client = OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
        }.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Apis::class.java)
    }

    @Provides
    @Singleton
    fun provideApiRepo(api: Apis): ApiRepo {
        return ApiRepoImpl(api)
    }

    @Provides
    @Singleton
    fun provideDownloadRepo(downloadDao: DownloadDao): DownloadRepo {
        return DownloadRepoImpl(
            downloadDao,
            CoroutineModule.provideDefaultDispatcher(),
            CoroutineModule.provideCoroutineScope(CoroutineModule.provideIoDispatcher())
        )
    }

    @Provides
    @Singleton
    fun provideTrackRepo(trackDao: TrackDao): TrackRepo {
        return TrackRepoImpl(
            trackDao,
            CoroutineModule.provideDefaultDispatcher(),
            CoroutineModule.provideCoroutineScope(CoroutineModule.provideIoDispatcher())
        )
    }

    @Provides
    @Singleton
    fun provideSmsApi(): SmsApis {

        val client = OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
        }.build()

        return Retrofit.Builder()
            .baseUrl(SMS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SmsApis::class.java)
    }

    @Provides
    @Singleton
    fun provideSmsApiRepo(smsApi: SmsApis): SmsApiRepo {
        return SmsApiRepoImpl(smsApi)
    }

    @Provides
    @Singleton
    fun providePaymentApi(): PaymentApis {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
        }.build()

        return Retrofit.Builder()
            .baseUrl(PAYMENT_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PaymentApis::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApiRepo(paymentApis: PaymentApis): PaymentApiRepo {
        return PaymentApiRepoImpl(paymentApis)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Provides
    @Singleton
    fun provideNagadApiRepo(nagadApis: NagadApis): NagadRepo {
        return NagadPayRepoImpl(nagadApis)
    }

    @Provides
    @Singleton
    fun provideNagadApi(): NagadApis {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
        }.build()

        return Retrofit.Builder()
            .baseUrl(NAGAD_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NagadApis::class.java)
    }


}