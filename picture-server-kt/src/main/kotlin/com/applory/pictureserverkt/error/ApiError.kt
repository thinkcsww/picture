package com.applory.pictureserverkt.error

import java.util.*
import kotlin.collections.HashMap

data class ApiError(
    val timestamp: Long = Date().time,
    val status: Int,
    val message: String,
    val url: String,
    var validationErrors: Map<String, String?> = HashMap()
)
