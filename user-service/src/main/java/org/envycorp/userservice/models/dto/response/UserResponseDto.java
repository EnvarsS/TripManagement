package org.envycorp.userservice.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.envycorp.userservice.models.entity.Role;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDto {
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
