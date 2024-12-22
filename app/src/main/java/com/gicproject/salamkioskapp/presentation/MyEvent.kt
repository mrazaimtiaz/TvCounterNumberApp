package com.gicproject.salamkioskapp.presentation


sealed class MyEvent {
    object GetBranches: MyEvent()
    object GetCounters: MyEvent()
    object GetDepartments: MyEvent()
    data class GetSelectServices(val deptId: String) : MyEvent()
}
