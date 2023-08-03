package com.andresuryana.aptasari.di

import android.content.Context
import androidx.room.Room
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
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(firebase: FirebaseSource, session: SessionHelper): UserRepository =
        UserRepositoryImpl(firebase, session)

    @Provides
    @Singleton
    fun provideQuizRepository(local: LocalDatabase): QuizRepository = QuizRepositoryImpl(local)

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
}