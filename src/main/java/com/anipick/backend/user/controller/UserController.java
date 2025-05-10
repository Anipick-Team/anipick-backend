package com.anipick.backend.user.controller;

import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.controller.dto.SignUpRequest;
import com.anipick.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signUp(@RequestBody SignUpRequest request) {
        ApiResponse<User> response = userService.signUp(request);
        return ResponseEntity.ok(response);
    }
}
