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
import com.andresuryana.aptasari.data.source.remote.ApiService
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(firebase: FirebaseSource, session: SessionHelper, local: LocalDatabase): UserRepository =
        UserRepositoryImpl(firebase, session, local)

    @Provides
    @Singleton
    fun provideQuizRepository(local: LocalDatabase, remote: ApiService): QuizRepository = QuizRepositoryImpl(local, remote)

    @Provides
    @Singleton
    fun provideFirebaseSource(): FirebaseSource =
        FirebaseSourceImpl(FirebaseAuth.getInstance())

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
}