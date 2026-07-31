package com.mateusantony.Gerenciador.user.controller;
import com.mateusantony.Gerenciador.user.dto.UserRequest;
import com.mateusantony.Gerenciador.user.dto.UserResponse;
import com.mateusantony.Gerenciador.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @RequestBody UserRequest request){

        UserResponse response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(java.security.Principal principal) {
        return ResponseEntity.ok(service.findByEmail(principal.getName()));
    }

}