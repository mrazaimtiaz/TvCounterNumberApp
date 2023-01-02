package com.gicproject.salamkioskapp.domain.model

import com.gicproject.salamkioskapp.domain.model.Department
import com.google.gson.annotations.SerializedName

data class Doctor(
    var id: String? = null,
    var nameEn: String? = null,
    var departmentEn: String? = null,
    var price: String? = null,
    var time: String? = null,
    var date: String? = null,
)