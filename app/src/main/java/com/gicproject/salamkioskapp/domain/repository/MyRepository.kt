package com.gicproject.salamkioskapp.domain.repository

import com.gicproject.salamkioskapp.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyRepository {
    suspend fun getBookTicket(
        serviceID: String,
        isHandicap: Boolean,
        isVip: Boolean,
        languageID: String,
        appointmentCode: String,
        isaapt: Boolean,
        refid: String,
        DoctorServiceID: String,
    ): List<BookTicketDto>?

    suspend fun getTicket(QueueID: Int,language: Int): List<GetTicketDto>?


    suspend fun getSelectServices(
        @Query("branchid")
        branchId: String,
        @Query("DeptParentID")
        deptId: String,
    ): List<SelectServiceDto>?

    suspend fun getSelectDepartments(
        branchId: String,
        deptParentId: String,
    ): List<SelectDepartmentDto>?

    suspend fun getDepartments(
    ): List<DepartmentDto>?

    suspend fun getCounters(
        branchId: String,
    ): List<CounterDto>?

    suspend fun getBranches(
    ): List<BranchDto>?


}



