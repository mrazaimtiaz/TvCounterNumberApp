package com.gicproject.salamkioskapp.presentation

import com.gicproject.salamkioskapp.domain.model.Department


data class InsertCivilIdScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val success: String = "",
)
