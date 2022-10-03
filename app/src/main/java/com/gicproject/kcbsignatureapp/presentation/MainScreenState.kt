package com.gicproject.kcbsignatureapp.presentation

import com.gicproject.kcbsignatureapp.domain.model.EmployeeData


data class MainScreenState(
    val employeeList: List<EmployeeData> = emptyList(),
    val isLoadingEmployeeList: Boolean = false,
    val isLoadingCivilId: Boolean = false,
    val civilIdPage: Boolean = true,
    val fingerPrintPage: Boolean = false,
    val signaturePage: Boolean = false,
    val employeeListPage: Boolean = false,
    val showToast: String = "",
    val civilidText: String = "",
    val serialNoText: String = "",
    val fullNameText: String = "",
    val firstNameText: String = "",
    val secondNameText: String = "",
    val thirdNameText: String = "",
    val fullNameArText: String = "",
    val firstNameArText: String = "",
    val secondNameArText: String = "",
    val thirdNameArText: String = "",
    val fullAddressText: String = "",
    val occupationText: String = "",
    val genderText: String = "",
    val bloodGroupText: String = "",
    val passportNoText: String = "",
    val dobText: String = "",
    val nationalityText: String = "",
    val expiryText: String = "",
    val tel1Text: String = "",
    val tel2Text: String = "",
    val emailText: String = "",
)
