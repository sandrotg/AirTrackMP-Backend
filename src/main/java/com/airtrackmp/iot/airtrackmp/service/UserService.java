package com.airtrackmp.iot.airtrackmp.service;

import com.airtrackmp.iot.airtrackmp.dto.UserRequest;
import com.airtrackmp.iot.airtrackmp.entity.User;
import com.airtrackmp.iot.airtrackmp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService (UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser (UserRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        return userRepo.save(user);
    }

    public List<User> findAllUsers() {
        return userRepo.findAll()
                .stream()
                .filter(user -> !user.isDeleted())
                .toList();
    }

    public User findUserById (Integer userId) {
        User user = userRepo.findById(userId).orElseThrow(
                () -> new RuntimeException("User not Found")
        );
        if (user.isDeleted()) {
            throw new RuntimeException("User is deleted");
        }
        return user;
    }

    public User updateUser (Integer userId, UserRequest request) {
        User user = findUserById(userId);
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        return userRepo.save(user);
    }

    public void removeUser(Integer userId) {
        User user = findUserById(userId);
        user.setDeleted(true);
        userRepo.save(user);
    }
}
