package com.example.bumpify.model

import java.util.*

data class ReportModel(
    val type: Int,
    val desc: String,
    val lon: Double,
    val lat: Double,
    val user: String
)
