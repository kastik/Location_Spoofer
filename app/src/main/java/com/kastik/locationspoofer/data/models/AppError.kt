package com.kastik.locationspoofer.data.models

data class AppError(
    val title: String,
    val message: String? = null,
    val action: () -> Unit,
    val dismiss: () -> Unit
)