package com.gicproject.salamkioskapp.di

import android.content.Context
import com.gicproject.salamkioskapp.data.remote.MyApi
import com.gicproject.dasdoctorcvapp.domain.use_case.*
import com.gicproject.salamkioskapp.common.Constants
import com.gicproject.salamkioskapp.data.repository.DataStoreRepositoryImpl
import com.gicproject.salamkioskapp.data.repository.MyRepositoryImpl
import com.gicproject.salamkioskapp.domain.repository.DataStoreRepository
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import com.gicproject.salamkioskapp.domain.use_case.GetDeparments
import com.gicproject.salamkioskapp.domain.use_case.MyUseCases
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

    @Provides
    @Singleton
    fun provideMyApi(): MyApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMyRepository(api: MyApi): MyRepository {
        return MyRepositoryImpl(api = api)
    }

    @Provides
    @Singleton
    fun provideSurveyUseCases(
        repository: MyRepository,
        dataStoreRepository: DataStoreRepository
    ): MyUseCases {
        return MyUseCases(
            getResult = GetResult(repository = repository),
            getDeparments = GetDeparments(repository = repository)
        )
    }
}