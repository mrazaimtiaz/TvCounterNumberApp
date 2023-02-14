package com.gicproject.salamkioskapp.domain.use_case

import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.domain.model.Counter
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetCounters @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(
        branchId: String
    ): Flow<Resource<List<Counter>>> = flow {
        try {
            emit(Resource.Loading())

            var counters = repository.getCounters(branchId)
            if (!counters.isNullOrEmpty()) {
                emit(Resource.Success(counters.map {
                    it.toCounter()
                }))
            } else {
                emit(Resource.Error("Empty Counter List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}