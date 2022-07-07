package com.applory.pictureserverkt.error

import java.util.*
import kotlin.collections.HashMap

data class ApiError(
    val timestamp: Long = Date().time,
    val status: Int? = null,
    val message: String? = null,
    val url: String? = null,
    var validationErrors: Map<String, String?> = HashMap()
)
