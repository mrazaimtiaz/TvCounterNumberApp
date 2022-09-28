package com.gicproject.kcbsignatureapp.data.repository


import com.gicproject.kcbsignatureapp.data.remote.MyApi
import com.gicproject.kcbsignatureapp.data.remote.dto.ResultDto
import com.gicproject.kcbsignatureapp.domain.model.EmployeeData
import com.gicproject.kcbsignatureapp.domain.model.GetPersonSendModel
import com.gicproject.kcbsignatureapp.domain.repository.MyRepository
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val api: MyApi
): MyRepository {

    override suspend fun getResult(): ResultDto? {
        return  api.getBranches()
    }

    override suspend fun getEmployeeData(getPersonSendModel: GetPersonSendModel): List<EmployeeData>? {
        return api.getEmployeeData(getPersonSendModel)
    }

    override suspend fun addEmployeeData(
        cid: String,
        filePart: MultipartBody.Part
    ): ResultDto? {
       return api.addEmployeeData(cid,filePart)
    }


}