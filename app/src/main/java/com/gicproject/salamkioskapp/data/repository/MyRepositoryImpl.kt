package com.gicproject.salamkioskapp.data.repository


import com.gicproject.salamkioskapp.data.remote.MyApi
import com.gicproject.salamkioskapp.data.remote.dto.BranchDto
import com.gicproject.salamkioskapp.data.remote.dto.CounterDto
import com.gicproject.salamkioskapp.data.remote.dto.DepartmentDto
import com.gicproject.salamkioskapp.data.remote.dto.ResultDto
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val api: MyApi
): MyRepository {



    override suspend fun getDepartments(branchId: String): List<DepartmentDto>? {
        return  api.getDepartments(branchId)
    }

    override suspend fun getCounters(branchId: String): List<CounterDto>? {
        return  api.getCounters(branchId)
    }

    override suspend fun getBranches(): List<BranchDto>? {
        return  api.getBranches()
    }


}