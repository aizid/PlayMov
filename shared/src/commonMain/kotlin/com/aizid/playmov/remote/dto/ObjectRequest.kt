package com.aizid.playmov.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class IntsRequest(
    val valueId: Int? = 0,
    val page: Int? = 0
)

@Serializable
data class StrIntRequest(
    val valueQuery: String? = "",
    val page: Int? = 0
)