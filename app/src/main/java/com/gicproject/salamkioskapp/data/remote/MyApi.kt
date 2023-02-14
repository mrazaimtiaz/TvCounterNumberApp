package com.gicproject.salamkioskapp.data.remote


import com.gicproject.salamkioskapp.data.remote.dto.BranchDto
import com.gicproject.salamkioskapp.data.remote.dto.CounterDto
import com.gicproject.salamkioskapp.data.remote.dto.DepartmentDto
import com.gicproject.salamkioskapp.data.remote.dto.ResultDto
import retrofit2.http.*

interface MyApi {
    @GET("api/departments")
    suspend fun getDepartments(
        @Query("branchid")
        branchId: String,
    ): List<DepartmentDto>?

    @GET("api/Counters")
    suspend fun getCounters(
        @Query("branchid")
        branchId: String,
    ): List<CounterDto>?

    @GET("api/Branches")
    suspend fun getBranches(
    ): List<BranchDto>?



}