package com.aizid.playmov.utils.network

import kotlinx.serialization.Serializable

@Serializable
data class FailureData(val code: Int, val message: String? = null)
