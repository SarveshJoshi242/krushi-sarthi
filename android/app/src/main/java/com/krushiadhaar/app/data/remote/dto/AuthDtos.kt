package com.krushiadhaar.app.data.remote.dto
data class LoginRequestDto(val phone: String, val password: String)
data class RegisterRequestDto(val fullName: String, val phone: String, val email: String?, val password: String, val role: String = "FARMER")
data class RefreshRequestDto(val refreshToken: String)
data class AuthResponseDto(val accessToken: String, val refreshToken: String)
data class UserResponseDto(val id: String, val fullName: String, val phone: String, val email: String?, val status: String, val roles: List<String>)
data class ApiErrorDto(val code: String, val message: String)
data class ApiResponseDto<T>(val success: Boolean, val data: T?, val error: ApiErrorDto?)
