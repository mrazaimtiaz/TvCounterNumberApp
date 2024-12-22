package com.gicproject.salamkioskapp.data.repository


import com.gicproject.salamkioskapp.data.remote.MyApi
import com.gicproject.salamkioskapp.data.remote.dto.*
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import javax.inject.Inject

class MyRepositoryImpl @Inject constructor(
    private val api: MyApi
) : MyRepository {



    override suspend fun getSelectServices(
        branchId: String,
        deptId: String,
    ): List<SelectServiceDto>? {
        return api.getSelectServices(branchId, deptId)
    }

    override suspend fun getDepartments(): List<DepartmentDto>? {
        return api.getDepartments()
    }

    override suspend fun getCounters(branchId: String): List<CounterDto>? {
        return api.getCounters(branchId)
    }

    override suspend fun getBranches(): List<BranchDto>? {
        return api.getBranches()
    }


}