package com.gicproject.salamkioskapp.domain.use_case


import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.data.remote.dto.DepartmentDto
import com.gicproject.salamkioskapp.domain.model.Department
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetDeparments @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(): Flow<Resource<List<Department>>> = flow {
        try {
            emit(Resource.Loading())

         //  var locations = repository.getLocations()
            var departments = listOf(DepartmentDto("1","Surgery Department"),DepartmentDto("1","Medical"))
            if (!departments.isNullOrEmpty()) {
                emit(Resource.Success(departments.map {
                    it.toDepartment()
                }))
            } else {
                emit(Resource.Error("Empty Department List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}