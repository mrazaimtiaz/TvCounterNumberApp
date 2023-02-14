package com.gicproject.salamkioskapp.domain.repository

import com.gicproject.salamkioskapp.data.remote.dto.BranchDto
import com.gicproject.salamkioskapp.data.remote.dto.CounterDto
import com.gicproject.salamkioskapp.data.remote.dto.DepartmentDto
import com.gicproject.salamkioskapp.data.remote.dto.ResultDto

interface MyRepository {



    suspend fun getDepartments(
        branchId: String,
    ): List<DepartmentDto>?

    suspend fun getCounters(
        branchId: String,
    ): List<CounterDto>?

    suspend fun getBranches(
    ): List<BranchDto>?


}



