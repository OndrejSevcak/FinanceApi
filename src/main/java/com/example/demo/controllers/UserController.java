package com.example.demo.controllers;

import com.example.demo.dto.user.CreateUserRequest;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest req, UriComponentsBuilder uriBuilder){
        UserResponse resp = userService.createUser(req);
        URI location = uriBuilder.path("/api/users/{id}").buildAndExpand(resp.getUserKey()).toUri();
        return ResponseEntity.created(location).body(resp);
    }
}
