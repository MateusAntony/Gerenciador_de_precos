package com.mateusantony.Gerenciador.user.mapper;
import com.mateusantony.Gerenciador.user.dto.UserRequest;
import com.mateusantony.Gerenciador.user.dto.UserResponse;
import com.mateusantony.Gerenciador.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class UserMapper {

    public  User toEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public  UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}