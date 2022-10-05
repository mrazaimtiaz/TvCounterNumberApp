package com.gicproject.kcbsignatureapp.domain.model

import com.google.gson.annotations.SerializedName

data class EmployeeSignature (
    @SerializedName("FILE_ID"            ) var FILEID            : String = "",
    @SerializedName("FILE_NAME"          ) var FILENAME          : String = "",
    @SerializedName("EMPLOYEE_SIGNATURE" ) var EMPLOYEESIGNATURE : String = "",
    @SerializedName("ATTACHMENT_DATE"    ) var ATTACHMENTDATE    : String = "",
    @SerializedName("ERROR_MESSAGE"    ) var ERRORMESSAGE    : String = "",

)
