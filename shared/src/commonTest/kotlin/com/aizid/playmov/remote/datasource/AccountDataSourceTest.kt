package com.aizid.playmov.remote.datasource

import com.aizid.playmov.remote.dto.account.LoginInput
import com.aizid.playmov.remote.dto.account.RegisterInput
import com.aizid.playmov.remote.dto.account.ValidateRegisterInput
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AccountDataSourceTest {

    private fun createMockClient(jsonResponse: String, onRequest: ((Map<String, List<String>>) -> Unit)? = null): HttpClient {
        return HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    onRequest?.invoke(request.headers.entries().associate { it.key to it.value })
                    respond(
                        content = ByteReadChannel(jsonResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    isLenient = true
                })
            }
        }
    }

    @Test
    fun register_returnsAuthTokenAndAccount() = runTest {
        val json = """
            {
                "data": {
                    "register": {
                        "authToken": "reg_token_12345",
                        "account": {
                            "accId": "acc_001",
                            "name": "Jane Doe"
                        }
                    }
                }
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = AccountDataSourceImpl(client)

        val input = RegisterInput(
            phoneNumber = "+628123456789",
            name = "Jane Doe",
            password = "password123",
            confirmPassword = "password123",
            deviceId = "dev_1",
            deviceName = "Android",
            deviceOs = "Android 14",
            ipAddress = "127.0.0.1"
        )

        val response = dataSource.register(input)
        assertNotNull(response)
        assertEquals("reg_token_12345", response.authToken)
        assertEquals("Jane Doe", response.account?.name)
    }

    @Test
    fun validateRegisterAccount_attachesBearerHeader() = runTest {
        val json = """
            {
                "data": {
                    "validateRegisterAccount": {
                        "authToken": "verified_auth_token_999"
                    }
                }
            }
        """.trimIndent()

        var capturedAuthHeader: String? = null
        val client = createMockClient(json) { headers ->
            capturedAuthHeader = headers[HttpHeaders.Authorization]?.firstOrNull()
        }
        val dataSource = AccountDataSourceImpl(client)

        val input = ValidateRegisterInput(
            code = "123456",
            phoneNumber = "+628123456789",
            name = "Jane Doe"
        )

        val response = dataSource.validateRegisterAccount(input, authToken = "reg_token_12345")
        assertEquals("Bearer reg_token_12345", capturedAuthHeader)
        assertEquals("verified_auth_token_999", response.authToken)
    }

    @Test
    fun login_returnsSessionToken() = runTest {
        val json = """
            {
                "data": {
                    "login": {
                        "authToken": "session_jwt_xyz",
                        "account": {
                            "accId": "acc_001"
                        }
                    }
                }
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = AccountDataSourceImpl(client)

        val input = LoginInput(
            identifier = "jane@example.com",
            password = "password123",
            deviceId = "dev_1",
            deviceName = "Android",
            deviceOs = "Android 14",
            ipAddress = "127.0.0.1"
        )

        val response = dataSource.login(input)
        assertEquals("session_jwt_xyz", response.authToken)
        assertEquals("acc_001", response.account?.accId)
    }

    @Test
    fun getAccountDetails_returnsAccountDetails() = runTest {
        val json = """
            {
                "data": {
                    "getAccountDetails": {
                        "accId": "3M7TRrdFxuz52BVh1eQ858fZ6HA",
                        "name": "Jane Doe",
                        "email": "jane@example.com",
                        "phoneNumber": "+628123456789"
                    }
                }
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = AccountDataSourceImpl(client)

        val details = dataSource.getAccountDetails("3M7TRrdFxuz52BVh1eQ858fZ6HA", "session_jwt_xyz")
        assertEquals("3M7TRrdFxuz52BVh1eQ858fZ6HA", details.accId)
        assertEquals("Jane Doe", details.name)
        assertEquals("jane@example.com", details.email)
        assertEquals("+628123456789", details.phoneNumber)
    }

    @Test
    fun graphQLError_throwsExceptionWithServerMessage() = runTest {
        val json = """
            {
                "errors": [
                    {"message": "Invalid credentials provided"}
                ]
            }
        """.trimIndent()
        val client = createMockClient(json)
        val dataSource = AccountDataSourceImpl(client)

        val input = LoginInput(
            identifier = "jane@example.com",
            password = "wrong",
            deviceId = "dev_1",
            deviceName = "Android",
            deviceOs = "Android 14",
            ipAddress = "127.0.0.1"
        )

        val exception = assertFailsWith<Exception> {
            dataSource.login(input)
        }
        assertEquals("Invalid credentials provided", exception.message)
    }
}
