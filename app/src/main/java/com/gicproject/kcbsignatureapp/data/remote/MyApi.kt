package com.gicproject.kcbsignatureapp.data.remote


import com.gicproject.kcbsignatureapp.common.Constants
import com.gicproject.kcbsignatureapp.data.remote.dto.ResultDto
import com.gicproject.kcbsignatureapp.domain.model.EmployeeData
import com.gicproject.kcbsignatureapp.domain.model.GetPersonSendModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface MyApi {
    @GET("api/Branches")
    suspend fun getBranches(
    ): ResultDto?

    @POST(Constants.BASE_URL + "mid/api/Paci/get_person_proc_out")
    suspend fun getEmployeeData(
        @Body getPersonSendModel: GetPersonSendModel
    ): List<EmployeeData>?

    @Multipart
    @POST(Constants.BASE_URL + "mid/api/Paci/ADD_EMPLOYEE_SIGNATURE")
    suspend fun addEmployeeData(
        @Query("cid") cid: String,
        @Part  filePart: MultipartBody.Part,
    ): ResultDto?


}