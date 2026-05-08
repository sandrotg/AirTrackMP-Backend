package com.airtrackmp.iot.airtrackmp.controller;

import com.airtrackmp.iot.airtrackmp.dto.UserRequest;
import com.airtrackmp.iot.airtrackmp.entity.User;
import com.airtrackmp.iot.airtrackmp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.saveUser(request));
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> findById(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.findUserById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Integer userId, @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> removeUser(@PathVariable Integer userId) {
        userService.removeUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}