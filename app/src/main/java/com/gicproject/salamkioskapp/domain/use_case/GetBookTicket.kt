package com.gicproject.salamkioskapp.domain.use_case


import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.data.remote.dto.BookTicketDto
import com.gicproject.salamkioskapp.data.remote.dto.DepartmentDto
import com.gicproject.salamkioskapp.data.remote.dto.DoctorDto
import com.gicproject.salamkioskapp.domain.model.BookTicket
import com.gicproject.salamkioskapp.domain.model.Department
import com.gicproject.salamkioskapp.domain.model.Doctor
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.http.Query
import java.io.IOException
import javax.inject.Inject

class GetBookTicket @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(
        serviceID: String,
        isHandicap: Boolean,
        isVip: Boolean,
        languageID: String,
        appointmentCode: String,
        isaapt: Boolean,
        refid: String,
        DoctorServiceID: String,
    ): Flow<Resource<BookTicket>> = flow {
        try {
            emit(Resource.Loading())

           var bookTicket = repository.getBookTicket(serviceID,isHandicap,isVip,languageID,appointmentCode,isaapt,refid,DoctorServiceID)
          //  var bookTicket = listOf(BookTicketDto(1,"Dr Emad",),BookTicketDto(1,"Dr Emad",), )


            if (!bookTicket.isNullOrEmpty()) {
                emit(Resource.Success(bookTicket[0].toBookTicket()))
            } else {
                emit(Resource.Error("Empty GetBookTicket List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}