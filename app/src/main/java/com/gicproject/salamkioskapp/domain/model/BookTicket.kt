package com.gicproject.salamkioskapp.domain.model

import com.google.gson.annotations.SerializedName

data class BookTicket(
    var NewPKID: Int? = null,
    var BookedNo: String? = null,
    var QueueSize: Int? = null,
    var EstimatedTime: Int? = null,
    var PrintTime: String? = null
)

