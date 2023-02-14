package com.gicproject.dasdoctorcvapp.domain.use_case


import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.domain.model.Branch
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetBranches @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(): Flow<Resource<List<Branch>>> = flow {
        try {
            emit(Resource.Loading())

            var branches = repository.getBranches()
            if (!branches.isNullOrEmpty()) {
                emit(Resource.Success(branches.map {
                    it.toBranch()
                }))
            } else {
                emit(Resource.Error("Empty Branch List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}