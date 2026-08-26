package com.krushiadhaar.user.dto;
import com.krushiadhaar.user.entity.User;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Data
public class UserResponse {
    private UUID id;
    private String fullName;
    private String phone;
    private String email;
    private String status;
    private List<String> roles;
    private LocalDateTime createdAt;
    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setRoles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()));
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
