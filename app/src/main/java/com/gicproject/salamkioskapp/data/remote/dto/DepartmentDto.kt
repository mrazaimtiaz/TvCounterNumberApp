package com.gicproject.salamkioskapp.data.remote.dto

import com.gicproject.salamkioskapp.domain.model.Department
import com.google.gson.annotations.SerializedName

data class DepartmentDto(
    @SerializedName("Department_PK_ID") var PKID: Int? = null,
    @SerializedName("DepartmentName_En_Screeens") var DepartmentNameEn: String? = null,
    @SerializedName("DepartmentName_Ar_Screeens") var DepartmentNameAr: String? = null,
    @SerializedName("PointX_Screen") var PointXScreen: Int? = null,
    @SerializedName("PointY_Screen") var PointYScreen: Int? = null,
    @SerializedName("FontSize_Screen") var FontSizeScreen: Int? = null,
    @SerializedName("FontColor_Screen") var FontColorScreen: String? = null,
    @SerializedName("FontName_Screen") var FontNameScreen: String? = null,
){
    fun toDepartment(): Department {
        return Department(
            PKID = PKID,
            DepartmentNameEn = DepartmentNameEn,
            DepartmentNameAr = DepartmentNameAr,
            PointXScreen = PointXScreen,
            PointYScreen = PointYScreen,
            FontSizeScreen = FontSizeScreen,
            FontColorScreen = FontColorScreen,
            FontNameScreen = FontNameScreen
        )
    }
}





