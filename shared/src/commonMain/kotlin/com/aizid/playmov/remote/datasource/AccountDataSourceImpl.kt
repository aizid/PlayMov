package com.aizid.playmov.remote.datasource

import com.aizid.playmov.remote.dto.account.*
import com.aizid.playmov.utils.constant.NetworkConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class AccountDataSourceImpl(
    private val client: HttpClient
) : AccountDataSource {

    private val graphqlEndpoint = NetworkConfig.GRAPHQL_URL

    override suspend fun register(input: RegisterInput): AuthResponse {
        val query = """
            mutation Register(${'$'}input: RegisterInput!) {
              register(input: ${'$'}input) {
                authToken
                account {
                  accId
                  name
                }
              }
            }
        """.trimIndent()

        val request = GraphQLRequest(
            query = query,
            variables = RegisterVariables(input = input)
        )

        val response: GraphQLResponse<RegisterData> = client.post(graphqlEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        val error = response.errors?.firstOrNull()?.message
        if (error != null) {
            throw Exception(error)
        }

        return response.data?.register ?: throw Exception("Empty register response from server")
    }

    override suspend fun validateRegisterAccount(
        input: ValidateRegisterInput,
        authToken: String?
    ): AuthResponse {
        val query = """
            mutation ValidateRegisterAccount(${'$'}input: ValidateRegisterInput!) {
              validateRegisterAccount(input: ${'$'}input) {
                authToken
              }
            }
        """.trimIndent()

        val request = GraphQLRequest(
            query = query,
            variables = ValidateRegisterVariables(input = input)
        )

        val response: GraphQLResponse<ValidateRegisterData> = client.post(graphqlEndpoint) {
            contentType(ContentType.Application.Json)
            if (!authToken.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
            setBody(request)
        }.body()

        val error = response.errors?.firstOrNull()?.message
        if (error != null) {
            throw Exception(error)
        }

        return response.data?.validateRegisterAccount
            ?: throw Exception("Empty validate register response from server")
    }

    override suspend fun login(input: LoginInput): AuthResponse {
        val query = """
            mutation Login(${'$'}input: LoginInput!) {
              login(input: ${'$'}input) {
                authToken
                account {
                  accId
                }
              }
            }
        """.trimIndent()

        val request = GraphQLRequest(
            query = query,
            variables = LoginVariables(input = input)
        )

        val response: GraphQLResponse<LoginData> = client.post(graphqlEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        val error = response.errors?.firstOrNull()?.message
        if (error != null) {
            throw Exception(error)
        }

        return response.data?.login ?: throw Exception("Empty login response from server")
    }

    override suspend fun getAccountDetails(accId: String, authToken: String?): AccountDetails {
        val query = """
            query GetAccountDetails(${'$'}accId: String!) {
              getAccountDetails(accId: ${'$'}accId) {
                accId
                name
                email
                phoneNumber
              }
            }
        """.trimIndent()

        val request = GraphQLRequest(
            query = query,
            variables = GetAccountDetailsVariables(accId = accId)
        )

        val response: GraphQLResponse<AccountDetailsData> = client.post(graphqlEndpoint) {
            contentType(ContentType.Application.Json)
            if (!authToken.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $authToken")
            }
            setBody(request)
        }.body()

        val error = response.errors?.firstOrNull()?.message
        if (error != null) {
            throw Exception(error)
        }

        return response.data?.getAccountDetails
            ?: throw Exception("Failed to retrieve account details")
    }
}
