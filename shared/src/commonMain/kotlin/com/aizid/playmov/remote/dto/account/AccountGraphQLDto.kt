package com.aizid.playmov.remote.dto.account

import kotlinx.serialization.Serializable

// --- Generic GraphQL Request & Response Envelope ---

@Serializable
data class GraphQLRequest<V>(
    val query: String,
    val variables: V
)

@Serializable
data class GraphQLResponse<T>(
    val data: T? = null,
    val errors: List<GraphQLError>? = null
)

@Serializable
data class GraphQLError(
    val message: String
)

// --- 1. Register Mutation Models ---

@Serializable
data class RegisterVariables(
    val input: RegisterInput
)

@Serializable
data class RegisterInput(
    val phoneNumber: String,
    val email: String? = null,
    val nip: String? = null,
    val password: String,
    val confirmPassword: String,
    val name: String,
    val deviceId: String,
    val deviceName: String,
    val deviceOs: String,
    val ipAddress: String
)

@Serializable
data class RegisterData(
    val register: AuthResponse
)

// --- 2. Validate Register Account Mutation Models ---

@Serializable
data class ValidateRegisterVariables(
    val input: ValidateRegisterInput
)

@Serializable
data class ValidateRegisterInput(
    val code: String,
    val phoneNumber: String,
    val name: String,
    val email: String? = null,
    val nip: String? = null
)

@Serializable
data class ValidateRegisterData(
    val validateRegisterAccount: AuthResponse
)

// --- 3. Login Mutation Models ---

@Serializable
data class LoginVariables(
    val input: LoginInput
)

@Serializable
data class LoginInput(
    val identifier: String,
    val password: String,
    val deviceId: String,
    val deviceName: String,
    val deviceOs: String,
    val ipAddress: String
)

@Serializable
data class LoginData(
    val login: AuthResponse
)

// --- 4. Get Account Details Query Models ---

@Serializable
data class GetAccountDetailsVariables(
    val accId: String
)

@Serializable
data class AccountDetailsData(
    val getAccountDetails: AccountDetails
)

// --- Shared Account & RBAC Entities ---

@Serializable
data class AuthResponse(
    val authToken: String,
    val account: AccountSummary? = null
)

@Serializable
data class AccountSummary(
    val accId: String,
    val name: String? = null
)

@Serializable
data class AccountDetails(
    val accId: String,
    val name: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val nip: String? = null,
    val roles: List<RoleDef> = emptyList(),
    val permissions: List<PermissionDef> = emptyList()
)

@Serializable
data class RoleDef(
    val roleDefId: String,
    val roleDefName: String
)

@Serializable
data class PermissionDef(
    val permId: String,
    val permName: String,
    val permResource: String,
    val permAction: String
)
