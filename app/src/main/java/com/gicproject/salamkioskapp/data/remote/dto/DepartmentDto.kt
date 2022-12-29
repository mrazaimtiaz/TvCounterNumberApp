package com.gicproject.salamkioskapp.data.remote.dto

import com.gicproject.salamkioskapp.domain.model.Department
import com.google.gson.annotations.SerializedName

data class DepartmentDto(
    @SerializedName("id") var id: String? = null,
    @SerializedName("nameEn") var nameEn: String? = null,
){
    fun toDepartment(): Department {
        return Department(
            id = id,
            nameEn = nameEn,
        )
    }
}
