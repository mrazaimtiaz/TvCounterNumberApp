package com.gicproject.salamkioskapp.domain.model

import com.google.gson.annotations.SerializedName

data class Department(
    var ParentID: Int? = null,
    var DepartmentNameEn: String? = null,
    var DepartmentNameAr: String? = null,
)

