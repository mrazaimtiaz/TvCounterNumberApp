package com.gicproject.kcbsignatureapp.domain.model

import com.google.gson.annotations.SerializedName

data class EmployeeData(@SerializedName("PERSON_ID"              ) var PERSONID             : String? = null,
                        @SerializedName("NATIONAL_IDENTIFIER"    ) var NATIONALIDENTIFIER   : String? = null,
                        @SerializedName("EMPLOYEE_NUMBER"        ) var EMPLOYEENUMBER       : String? = null,
                        @SerializedName("FULL_NAME"              ) var FULLNAME             : String? = null,
                        @SerializedName("USER_ID"                ) var USERID               : String? = null,
                        @SerializedName("USER_NAME"              ) var USERNAME             : String? = null,
                        @SerializedName("ASSIGNMENT_ID"          ) var ASSIGNMENTID         : String? = null,
                        @SerializedName("ORGANIZATION_ID"        ) var ORGANIZATIONID       : String? = null,
                        @SerializedName("ORGANIZATION_NAME"      ) var ORGANIZATIONNAME     : String? = null,
                        @SerializedName("JOB_ID"                 ) var JOBID                : String? = null,
                        @SerializedName("JOB_NAME"               ) var JOBNAME              : String? = null,
                        @SerializedName("TAKLIF_TYPE_CODE"       ) var TAKLIFTYPECODE       : String? = null,
                        @SerializedName("TAKLIF_TYPE_NAME"       ) var TAKLIFTYPENAME       : String? = null,
                        @SerializedName("TAKLIF_DEPARTMENT_ID"   ) var TAKLIFDEPARTMENTID   : String? = null,
                        @SerializedName("TAKLIF_DEPARTMENT_NAME" ) var TAKLIFDEPARTMENTNAME : String? = null,
                        @SerializedName("TAKLIF_JOB_ID"          ) var TAKLIFJOBID          : String? = null,
                        @SerializedName("TAKLIF_JOB_NAME"        ) var TAKLIFJOBNAME        : String? = null,
                        @SerializedName("TAKLIF_TECH_JOB_ID"     ) var TAKLIFTECHJOBID      : String? = null,
                        @SerializedName("TAKLIF_TECH_JOB_NAME"   ) var TAKLIFTECHJOBNAME    : String? = null,
                        @SerializedName("SIGNATURE_EXISTS"       ) var SIGNATUREEXISTS      : String? = null

)
