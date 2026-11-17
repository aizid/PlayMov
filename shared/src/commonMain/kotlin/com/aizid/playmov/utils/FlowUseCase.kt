package com.aizid.playmov.utils

import com.aizid.playmov.utils.network.FailureData
import com.aizid.playmov.utils.network.Resource
import com.aizid.playmov.utils.network.NetworkCodes
import com.aizid.playmov.utils.exeption.ForbiddenException
import com.aizid.playmov.utils.exeption.HttpException
import com.aizid.playmov.utils.exeption.NoNetworkException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

abstract class FlowUseCase<in P, R> {
    suspend operator fun invoke(parameters: P? = null): Flow<Resource<R>> = execute(parameters)
        .catch { e -> emit(Resource.Failure(handleException(e))) }

    protected abstract suspend fun execute(parameters: P? = null): Flow<Resource<R>>

    private fun handleException(throwable: Throwable): FailureData = when (throwable) {
        is NoNetworkException -> FailureData(
            code = NetworkCodes.NO_CONNECTION,
            message = throwable.message
        )
        is TimeoutCancellationException -> FailureData(
            code = NetworkCodes.TIMEOUT_ERROR,
            message = throwable.message
        )
        is ForbiddenException -> FailureData(
            code = NetworkCodes.FORBIDDEN,
            message = throwable.message
        )
        is HttpException -> FailureData(
            code = throwable.code(),
            message = throwable.message
        )
        is NullPointerException -> FailureData(
            code = NetworkCodes.HTTP_NO_CONTENT,
            message = throwable.message
        )
        else -> FailureData(
            code = NetworkCodes.GENERIC_ERROR,
            message = throwable.message
        )
    }
}
