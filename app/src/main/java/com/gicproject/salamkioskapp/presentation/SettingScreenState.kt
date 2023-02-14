package com.gicproject.salamkioskapp.presentation

import com.gicproject.salamkioskapp.domain.model.Branch
import com.gicproject.salamkioskapp.domain.model.Counter
import com.gicproject.salamkioskapp.domain.model.Department


data class SettingScreenState(
    val isLoading: Boolean = false,
    val isLoadingPagination: Boolean = false,
    val branches: List<Branch> = emptyList(),
    val counters: List<Counter> = emptyList(),
    val department: List<Department> = emptyList(),
    val error: String = ""
)
