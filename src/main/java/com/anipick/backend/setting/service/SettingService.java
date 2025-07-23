package com.anipick.backend.setting.service;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.setting.dto.ChangeEmailRequest;
import com.anipick.backend.setting.dto.ChangeNicknameRequest;
import com.anipick.backend.setting.dto.ChangePasswordRequest;
import com.anipick.backend.setting.util.DeletedUserNicknameGenerator;
import com.anipick.backend.setting.util.NicknameValidator;
import com.anipick.backend.user.mapper.UserMapper;
import com.anipick.backend.user.util.SignUpValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void changeNickname(Long userId, ChangeNicknameRequest request) {
        String newNickName = request.getNickname();

        if(!NicknameValidator.checkValidateNickname(newNickName)) {
            throw new CustomException(ErrorCode.NICKNAME_INVALID_FORMAT);
        }

        if(userMapper.existsByNickname(newNickName)) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATE);
        }

        userMapper.updateUserNickname(userId, newNickName);
    }

    public void changeEmail(CustomUserDetails user, ChangeEmailRequest request) {
        String newEmail = request.getNewEmail();
        String requestPassword = request.getPassword();

        if (checkDuplicateEmail(newEmail)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        if (!SignUpValidator.checkValidationEmail(newEmail)) {
            throw new CustomException(ErrorCode.EMAIL_INVALID_FORMAT);
        }

        if(!passwordEncoder.matches(requestPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_MISMATCH);
        }

        userMapper.updateUserEmail(user.getUserId(), newEmail);
    }

    private boolean checkDuplicateEmail(String email) {
        return userMapper.findByEmail(email).isPresent();
    }

    public void changePassword(CustomUserDetails user, ChangePasswordRequest request) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        String confirmNewPassword = request.getConfirmNewPassword();

        if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CHANGE_OLD_MISMATCH);
        }

        if(!SignUpValidator.checkValidationPassword(newPassword)) {
            throw new CustomException(ErrorCode.PASSWORD_INVALID_FORMAT);
        }

        if(!newPassword.equals(confirmNewPassword)) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        userMapper.updateUserPassword(user.getUser().getEmail(), passwordEncoder.encode(newPassword));
    }

    public void userWithdrawal(CustomUserDetails user) {
        String deletedUserNickname = DeletedUserNicknameGenerator.generateDeletedUserNickname();
        userMapper.updateUserByWithdrawal(user.getUserId(), deletedUserNickname);
    }
}
