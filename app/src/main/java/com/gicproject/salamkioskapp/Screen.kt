package com.gicproject.salamkioskapp

sealed class Screen(val route: String){
    object SelectOptionScreen: Screen("select_option_screen")
}
