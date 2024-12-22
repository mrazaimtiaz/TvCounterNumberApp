package com.gicproject.salamkioskapp.data.remote


import com.gicproject.salamkioskapp.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MyApi {



    @GET("api/GetServices")
    suspend fun getSelectServices(
        @Query("branchid")
        branchId: String,
        @Query("DeptParentID")
        deptId: String,
    ): List<SelectServiceDto>?


    @GET("api/dept")
    suspend fun getDepartments(
    ): List<DepartmentDto>?

    @GET("api/Counters")
    suspend fun getCounters(
        @Query("branchid")
        branchId: String,
    ): List<CounterDto>?

    @GET("api/getbranches")
    suspend fun getBranches(
    ): List<BranchDto>?



}