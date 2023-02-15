package com.gicproject.salamkioskapp.data.remote.dto

import com.gicproject.salamkioskapp.domain.model.BookTicket
import com.gicproject.salamkioskapp.domain.model.Department
import com.google.gson.annotations.SerializedName
data class BookTicketDto(

    @SerializedName("NewPKID"       ) var NewPKID       : Int?    = null,
    @SerializedName("BookedNo"      ) var BookedNo      : String? = null,
    @SerializedName("QueueSize"     ) var QueueSize     : Int?    = null,
    @SerializedName("EstimatedTime" ) var EstimatedTime : Int?    = null,
    @SerializedName("PrintTime"     ) var PrintTime     : String? = null
){
    fun toBookTicket(): BookTicket {
        return BookTicket(
            NewPKID = NewPKID,
            BookedNo = BookedNo,
            QueueSize = QueueSize,
            EstimatedTime =EstimatedTime,
            PrintTime = PrintTime
        )
    }
}





