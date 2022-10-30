package com.gicproject.salamkioskapp.domain.repository

import com.gicproject.salamkioskapp.data.remote.dto.ResultDto

interface MyRepository {

    suspend fun getResult(
    ): ResultDto?


}



