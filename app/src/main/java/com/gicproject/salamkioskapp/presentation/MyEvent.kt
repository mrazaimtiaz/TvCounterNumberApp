package com.gicproject.salamkioskapp.presentation


sealed class MyEvent {
    object GetDepartment: MyEvent()
    object GetDoctor: MyEvent()
}
