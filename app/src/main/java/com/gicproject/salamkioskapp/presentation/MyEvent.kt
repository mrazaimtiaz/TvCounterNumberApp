package com.gicproject.salamkioskapp.presentation


sealed class MyEvent {
    object GetBranches: MyEvent()
    object GetCounters: MyEvent()
    object GetDepartments: MyEvent()
    object GetSelectDepartments: MyEvent()
    data class GetSelectServices(val deptId: String) : MyEvent()
    object GetDoctor: MyEvent()
    object GetPrintTicket: MyEvent()
    data class GetBookTicket(
        val serviceID: String,
        val isHandicap: Boolean,
        val isVip: Boolean,
        val languageID: String,
        val appointmentCode: String,
        val  isaapt: Boolean,
        val  refid: String,
        val DoctorServiceID: String,val ticketDesignId: String) : MyEvent()
}
