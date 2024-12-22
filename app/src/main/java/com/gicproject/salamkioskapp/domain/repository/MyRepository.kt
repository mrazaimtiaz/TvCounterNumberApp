package com.gicproject.salamkioskapp.domain.repository

import com.gicproject.salamkioskapp.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyRepository {

    suspend fun getSelectServices(
        @Query("branchid")
        branchId: String,
        @Query("DeptParentID")
        deptId: String,
    ): List<SelectServiceDto>?

    suspend fun getDepartments(
    ): List<DepartmentDto>?

    suspend fun getCounters(
        branchId: String,
    ): List<CounterDto>?

    suspend fun getBranches(
    ): List<BranchDto>?


}



