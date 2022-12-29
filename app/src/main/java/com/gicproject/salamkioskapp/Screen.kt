package com.gicproject.salamkioskapp

sealed class Screen(val route: String){
    object SettingScreen: Screen("setting_screen")
    object SelectOptionScreen: Screen("select_option_screen")
    object SelectDoctorScreen: Screen("select_doctor_screen")
    object SelectDoctorTimeScreen: Screen("select_doctor_time_screen")
    object SelectDepartmentScreen: Screen("select_department_screen")
    object InsertKnetScreen: Screen("insert_knet_screen")
    object DoctorPayScreen: Screen("doctor_pay_screen")
    object InsertCivilIdScreen: Screen("insert_civilid_screen")
    object SelectServiceScreen: Screen("select_service_screen")
    object SelectChildServiceScreen: Screen("select_child_service_screen")
    object LinkPayScreen: Screen("link_pay_screen")
}
