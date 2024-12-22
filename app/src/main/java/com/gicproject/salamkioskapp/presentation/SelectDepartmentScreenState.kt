package com.gicproject.salamkioskapp.presentation

import com.gicproject.salamkioskapp.domain.model.SelectService


data class SelectDepartmentScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val departments: List<SelectService> = emptyList(),
)
