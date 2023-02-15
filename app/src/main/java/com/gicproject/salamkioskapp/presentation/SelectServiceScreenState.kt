package com.gicproject.salamkioskapp.presentation

import com.gicproject.salamkioskapp.domain.model.Department
import com.gicproject.salamkioskapp.domain.model.Doctor
import com.gicproject.salamkioskapp.domain.model.SelectService


data class SelectServiceScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val success: String = "",
    val services: List<SelectService> = emptyList(),
)
