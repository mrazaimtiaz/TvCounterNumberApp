package com.gicproject.dasdoctorcvapp.domain.use_case


import com.gicproject.kcbsignatureapp.common.Resource
import com.gicproject.kcbsignatureapp.data.remote.dto.ResultDto
import com.gicproject.kcbsignatureapp.domain.model.EmployeeData
import com.gicproject.kcbsignatureapp.domain.model.GetPersonSendModel
import com.gicproject.kcbsignatureapp.domain.model.ResultData
import com.gicproject.kcbsignatureapp.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetEmployeeData @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(getPersonSendModel: GetPersonSendModel): Flow<Resource<EmployeeData?>> = flow {
        try {
            emit(Resource.Loading())

            var result = repository.getEmployeeData(getPersonSendModel)
            if(!result.isNullOrEmpty()){
                emit(Resource.Success(result[0]))

            }else{
                emit(Resource.Error("Empty List"))
            }

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}