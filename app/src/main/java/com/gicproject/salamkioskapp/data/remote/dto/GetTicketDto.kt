package com.gicproject.salamkioskapp.data.remote.dto

import com.gicproject.salamkioskapp.domain.model.BookTicket
import com.gicproject.salamkioskapp.domain.model.Department
import com.gicproject.salamkioskapp.domain.model.GetTicket
import com.google.gson.annotations.SerializedName
data class GetTicketDto(

    @SerializedName("Ticket"       ) var Ticket       : String?    = null,
){
    fun toGetTicket(): GetTicket {
        return GetTicket(
            Ticket = Ticket,
        )
    }
}





