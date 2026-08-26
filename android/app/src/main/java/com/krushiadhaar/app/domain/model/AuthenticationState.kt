package com.krushiadhaar.app.domain.model
sealed class AuthenticationState {
    object Loading : AuthenticationState()
    object Authenticated : AuthenticationState()
    object Unauthenticated : AuthenticationState()
}
