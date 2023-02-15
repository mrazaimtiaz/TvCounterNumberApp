package com.gicproject.salamkioskapp.domain.model

import com.google.gson.annotations.SerializedName

data class SelectDepartment(
    var DepartmentPKID: Int? = null,
    var DepartmentNameEN: String? = null,
    var DepartmentNameAR: String? = null,
    var DepartmentLocation: String? = null,
    var DepartmentDescriptions: String? = null,
    var DepartmentParentID: Int? = null,
    var DepartmentClinicID: Int? = null
): java.io.Serializable

