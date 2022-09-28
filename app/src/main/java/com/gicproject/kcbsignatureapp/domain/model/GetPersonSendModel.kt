package com.gicproject.kcbsignatureapp.domain.model

import androidx.core.graphics.drawable.RoundedBitmapDrawable
import retrofit2.http.Field

data class GetPersonSendModel(
    var p_proc_name: String,
    var P_NATIONAL_ID: String,
)


