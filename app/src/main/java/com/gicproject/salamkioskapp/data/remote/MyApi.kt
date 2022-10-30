package com.gicproject.salamkioskapp.data.remote


import com.gicproject.salamkioskapp.data.remote.dto.ResultDto
import retrofit2.http.*

interface MyApi {
    @GET("api/Branches")
    suspend fun getBranches(
    ): ResultDto?



}