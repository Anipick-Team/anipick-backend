package com.anipick.backend.admin.service;

import com.anipick.backend.admin.domain.Admin;
import com.anipick.backend.admin.dto.AdminUsernamePasswordRequestDto;
import com.anipick.backend.admin.mapper.AdminMapper;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.token.dto.TokenResponse;
import com.anipick.backend.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public void signup(AdminUsernamePasswordRequestDto request) {
        if (existAccount(request)) {
            throw new CustomException(ErrorCode.ALREADY_ADMIN_USERNAME);
        }

        String username = request.getUsername();
        String password = request.getPassword();

        String encodePassword = passwordEncoder.encode(password);
        Admin account = Admin.createEncoderPasswordAccount(username, encodePassword);

        adminMapper.insertAdminAccount(account);
    }

    private Boolean existAccount(AdminUsernamePasswordRequestDto request) {
        return adminMapper.existAdminAccountUsername(request.getUsername());
    }

    public TokenResponse login(AdminUsernamePasswordRequestDto request) {
        Admin admin = adminMapper.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_ACCOUNT_NOT_FOUNT_BY_USERNAME));

        if (admin.getIsActive() == false) {
            throw new CustomException(ErrorCode.ADMIN_ACCOUNT_DEACTIVATE);
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new CustomException(ErrorCode.ADMIN_ACCOUNT_LOGIN_FAIL);
        }

        return tokenService.generateAndSaveTokens(request.getUsername());
    }
}
