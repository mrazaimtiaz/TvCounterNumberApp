package com.gicproject.salamkioskapp.presentation


sealed class MyEvent {
    object GetBranches: MyEvent()
    object GetCounters: MyEvent()
    object GetDepartments: MyEvent()
    object GetDepartment: MyEvent()
    object GetDoctor: MyEvent()
    object GetPrintTicket: MyEvent()
}
