package com.krushiadhaar.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krushiadhaar.common.response.ApiResponse;
import com.krushiadhaar.common.response.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        ApiResponse<Object> apiResponse = ApiResponse.error(
                new ApiError("UNAUTHORIZED", "Authentication failed: " + authException.getMessage())
        );
        
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
