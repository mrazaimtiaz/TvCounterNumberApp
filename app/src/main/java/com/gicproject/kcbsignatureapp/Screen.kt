package com.gicproject.kcbsignatureapp

sealed class Screen(val route: String){
    object MainScreen: Screen("main_screen")
}
