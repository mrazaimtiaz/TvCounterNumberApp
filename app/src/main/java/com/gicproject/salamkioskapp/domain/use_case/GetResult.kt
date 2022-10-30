package com.gicproject.dasdoctorcvapp.domain.use_case


import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.domain.model.ResultData
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetResult @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(): Flow<Resource<ResultData?>> = flow {
        try {
            emit(Resource.Loading())

            var result = repository.getResult()
                emit(Resource.Success(result?.toResult()))

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}