package com.gicproject.salamkioskapp.presentation


data class SettingScreenState(
    val isLoading: Boolean = false,
    val isLoadingPagination: Boolean = false,
    val error: String = ""
)
