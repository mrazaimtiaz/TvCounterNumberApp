package com.gicproject.salamkioskapp

sealed class Screen(val route: String){
    object SelectOptionScreen: Screen("select_option_screen")
    object SelectDoctorScreen: Screen("select_doctor_screen")
    object SelectDoctorTimeScreen: Screen("select_doctor_time_screen")
    object SelectDepartmentScreen: Screen("select_department_screen")
    object InsertKnetScreen: Screen("insert_knet_screen")
    object DoctorPayScreen: Screen("doctor_pay_screen")
}
