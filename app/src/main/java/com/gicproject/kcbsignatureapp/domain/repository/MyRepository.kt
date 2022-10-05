package com.gicproject.kcbsignatureapp.domain.repository

import com.gicproject.kcbsignatureapp.data.remote.dto.ResultDto
import com.gicproject.kcbsignatureapp.domain.model.EmployeeData
import com.gicproject.kcbsignatureapp.domain.model.EmployeeSignature
import com.gicproject.kcbsignatureapp.domain.model.GetPersonSendModel
import okhttp3.MultipartBody
import retrofit2.Response

interface MyRepository {

    suspend fun getResult(
    ): ResultDto?

    suspend fun getEmployeeData(
         getPersonSendModel: GetPersonSendModel
    ): List<EmployeeData>?

    suspend fun getEmployeeSignature(
        getPersonSendModel: GetPersonSendModel
    ): List<EmployeeSignature>?

    suspend fun addEmployeeData(
       cid: String,
       filePart: MultipartBody.Part,
    ): ResultDto?

}



