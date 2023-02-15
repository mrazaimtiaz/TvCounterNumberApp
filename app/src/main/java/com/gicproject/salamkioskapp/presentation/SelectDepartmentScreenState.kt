package com.gicproject.salamkioskapp.presentation

import com.gicproject.salamkioskapp.domain.model.Department
import com.gicproject.salamkioskapp.domain.model.SelectDepartment


data class SelectDepartmentScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val departments: List<SelectDepartment> = emptyList(),
)
