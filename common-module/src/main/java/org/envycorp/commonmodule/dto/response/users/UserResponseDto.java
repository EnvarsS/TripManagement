package org.envycorp.commonmodule.dto.response.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private RoleResponseDto role;
    private LocalDateTime createdAt;
}
