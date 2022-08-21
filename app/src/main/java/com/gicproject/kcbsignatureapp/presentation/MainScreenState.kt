package com.gicproject.kcbsignatureapp.presentation



data class MainScreenState(
    val isLoadingCivilId: Boolean = false,
    val civilIdPage: Boolean = true,
    val fingerPrintPage: Boolean = false,
    val signaturePage: Boolean = false,
    val error: String = "",
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
