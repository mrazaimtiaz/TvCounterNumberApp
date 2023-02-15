package com.gicproject.salamkioskapp.domain.use_case


import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.data.remote.dto.BookTicketDto
import com.gicproject.salamkioskapp.data.remote.dto.GetTicketDto
import com.gicproject.salamkioskapp.domain.repository.MyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.http.Query
import java.io.IOException
import javax.inject.Inject

class GetTicket @Inject constructor(
    private val repository: MyRepository
) {
    operator fun invoke(
        queueId: Int,
        languageId: Int,
    ): Flow<Resource<com.gicproject.salamkioskapp.domain.model.GetTicket>> = flow {
        try {
            emit(Resource.Loading())

         //  var getTicket = repository.getTicket(queueId,languageId,)
            var getTicket = listOf(
                GetTicketDto(""),GetTicketDto("Dr Emad",),

            )
            if (!getTicket.isNullOrEmpty()) {
                emit(Resource.Success(getTicket[0].toGetTicket()))
            } else {
                emit(Resource.Error("Empty GetTicket List."))
            }
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}