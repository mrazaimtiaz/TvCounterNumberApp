package com.gicproject.salamkioskapp

sealed class Screen(val route: String){
    object SettingScreen: Screen("setting_screen")
    object SelectDepartmentScreen: Screen("select_department_screen")
}
