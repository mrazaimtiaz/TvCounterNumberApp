package com.gicproject.kcbsignatureapp.domain.use_case


import com.gicproject.kcbsignatureapp.common.Resource
import com.gicproject.kcbsignatureapp.data.remote.dto.ResultDto
import com.gicproject.kcbsignatureapp.domain.model.EmployeeData
import com.gicproject.kcbsignatureapp.domain.model.GetPersonSendModel
import com.gicproject.kcbsignatureapp.domain.model.ResultData
import com.gicproject.kcbsignatureapp.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddEmployeeData @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(cid: String,filePart: MultipartBody.Part): Flow<Resource<ResultData?>> = flow {
        try {
            emit(Resource.Loading())

            var result = repository.addEmployeeData(cid,filePart)

            emit(Resource.Success(result?.toResult()))

        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}