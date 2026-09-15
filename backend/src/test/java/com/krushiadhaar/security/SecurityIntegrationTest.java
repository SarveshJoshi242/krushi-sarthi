package com.krushiadhaar.security;

import com.krushiadhaar.user.entity.Role;
import com.krushiadhaar.user.entity.User;
import com.krushiadhaar.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "JWT_SECRET=thisisaverylongsecretkeyforjwtsignaturepurposesonly"
})
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UserRepository userRepository;
    @Autowired private JwtService jwtService;

    @Test
    void testMultiUserIdentityIsolation() throws Exception {
        User userA = new User(); userA.setId(UUID.randomUUID()); userA.setPhone("1111111111"); userA.setRoles(Set.of(new Role(1, "FARMER")));
        User userB = new User(); userB.setId(UUID.randomUUID()); userB.setPhone("2222222222"); userB.setRoles(Set.of(new Role(1, "BUYER")));

        when(userRepository.findByPhone("1111111111")).thenReturn(Optional.of(userA));
        when(userRepository.findByPhone("2222222222")).thenReturn(Optional.of(userB));

        String tokenA = jwtService.generateToken(new CustomUserDetails(userA));
        String tokenB = jwtService.generateToken(new CustomUserDetails(userB));

        // User A request
        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + tokenA))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.phone").value("1111111111"));

        // User B request
        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + tokenB))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.phone").value("2222222222"));

        // Unauthenticated request
        mockMvc.perform(get("/api/v1/users/me"))
            .andExpect(status().isUnauthorized());
    }
}
