package com.mateusantony.Gerenciador.user.service;
import com.mateusantony.Gerenciador.user.dto.UserRequest;
import com.mateusantony.Gerenciador.user.dto.UserResponse;
import com.mateusantony.Gerenciador.user.entity.User;
import com.mateusantony.Gerenciador.user.mapper.UserMapper;
import com.mateusantony.Gerenciador.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse create(UserRequest request){


        User user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = repository.save(user);

        return mapper.toResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return mapper.toResponse(user);
    }

}