package com.gicproject.salamkioskapp.domain.use_case

import com.gicproject.dasdoctorcvapp.domain.use_case.GetResult

data class MyUseCases(
    val getResult: GetResult,
    val getDeparments: GetDeparments,
    val getDoctors: GetDoctors,
)
