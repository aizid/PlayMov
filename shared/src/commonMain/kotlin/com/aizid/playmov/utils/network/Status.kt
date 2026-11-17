package com.aizid.playmov.utils.network

import kotlinx.serialization.Serializable

@Serializable
enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}
