package com.example.demo.services;

import com.example.demo.dto.user.CreateUserRequest;
import com.example.demo.dto.user.UserResponse;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest req) {

        User u = new User();
        u.setEmail(req.getEmail());
        u.setPassword(req.getPassword());
        u.setNickName(req.getNickName());
        u.setLevel(1);

        User saved = userRepository.save(u);

        return new UserResponse(saved.getUserKey(), saved.getEmail(), saved.getNickName(), saved.getLevel());
    }
}
