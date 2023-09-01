package com.andresuryana.aptasari.di

import android.content.Context
import androidx.room.Room
import com.andresuryana.aptasari.BuildConfig
import com.andresuryana.aptasari.data.repository.QuizRepository
import com.andresuryana.aptasari.data.repository.QuizRepositoryImpl
import com.andresuryana.aptasari.data.repository.UserRepository
import com.andresuryana.aptasari.data.repository.UserRepositoryImpl
import com.andresuryana.aptasari.data.source.firebase.FirebaseSource
import com.andresuryana.aptasari.data.source.firebase.FirebaseSourceImpl
import com.andresuryana.aptasari.data.source.local.DatabaseContract
import com.andresuryana.aptasari.data.source.local.LocalDatabase
import com.andresuryana.aptasari.data.source.prefs.SessionHelper
import com.andresuryana.aptasari.data.source.prefs.SessionHelperImpl
import com.andresuryana.aptasari.data.source.remote.ANNService
import com.andresuryana.aptasari.data.source.remote.ApiService
import com.andresuryana.aptasari.util.FileDownloader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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

    @Provides
    @Singleton
    fun provideUserRepository(
        firebase: FirebaseSource,
        session: SessionHelper,
        local: LocalDatabase
    ): UserRepository =
        UserRepositoryImpl(firebase, session, local)

    @Provides
    @Singleton
    fun provideQuizRepository(
        local: LocalDatabase,
        remote: ApiService,
        annService: ANNService
    ): QuizRepository = QuizRepositoryImpl(local, remote, annService)

    @Provides
    @Singleton
    fun provideFirebaseSource(): FirebaseSource =
        FirebaseSourceImpl(FirebaseAuth.getInstance(), FirebaseDatabase.getInstance())

    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext context: Context): LocalDatabase =
        Room.databaseBuilder(context, LocalDatabase::class.java, DatabaseContract.DATABASE_NAME)
            .build()

    @Provides
    @Singleton
    fun provideSessionHelper(@ApplicationContext context: Context): SessionHelper =
        SessionHelperImpl(context)

    @Provides
    @Singleton
    fun provideApiService(): ApiService = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideANNService(): ANNService = Retrofit.Builder()
        .baseUrl(BuildConfig.ANN_SERVICE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(
                            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                            else HttpLoggingInterceptor.Level.NONE
                        )
                )
                .build()
        )
        .build().create(ANNService::class.java)

    @Provides
    @Singleton
    fun provideFileDownloader(@ApplicationContext context: Context): FileDownloader =
        FileDownloader(context.filesDir)
}