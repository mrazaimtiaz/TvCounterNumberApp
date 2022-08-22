package com.gicproject.kcbsignatureapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.gicproject.kcbsignatureapp.data.repository.DataStoreRepositoryImpl
import com.gicproject.kcbsignatureapp.domain.repository.DataStoreRepository
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
class AppModule {

    @Singleton
    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext app: Context
    ): DataStoreRepository = DataStoreRepositoryImpl(app)


}