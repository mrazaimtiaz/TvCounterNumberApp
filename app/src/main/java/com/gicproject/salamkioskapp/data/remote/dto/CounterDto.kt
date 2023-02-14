package com.gicproject.salamkioskapp.data.remote.dto

import com.gicproject.salamkioskapp.domain.model.Counter
import com.google.gson.annotations.SerializedName

data class CounterDto(
    @SerializedName("Counter_PK_ID") var PKID: Int? = null,
    @SerializedName("Counter_Description") var CounterDes: String? = null,
    @SerializedName("Counter_MachineName") var CounterName: String? = null
){
    fun toCounter(): Counter {
        return Counter(
            PKID = PKID,
            CounterDes = CounterDes,
            CounterName = CounterName,
        )
    }
}


