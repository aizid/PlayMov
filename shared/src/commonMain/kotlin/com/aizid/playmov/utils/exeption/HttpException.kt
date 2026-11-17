package com.aizid.playmov.utils.exeption

class HttpException(private val statusCode: Int, message: String? = null) : RuntimeException(message) {
    fun code(): Int = statusCode
}
