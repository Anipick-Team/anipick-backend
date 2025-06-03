package com.anipick.backend.auth.service;

import com.anipick.backend.auth.component.AuthEmailFormatInitializer;
import com.anipick.backend.auth.component.VerificationNumberInitializer;
import com.anipick.backend.auth.domain.EmailDefaults;
import com.anipick.backend.auth.dto.AuthCodeRequest;
import com.anipick.backend.auth.dto.AuthEmailRequest;
import com.anipick.backend.auth.dto.AuthPasswordRequest;
import com.anipick.backend.common.exception.CustomException;
import com.anipick.backend.common.exception.ErrorCode;
import com.anipick.backend.user.domain.LoginFormat;
import com.anipick.backend.user.domain.User;
import com.anipick.backend.user.mapper.UserMapper;
import com.anipick.backend.user.service.SignUpValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthEmailFormatInitializer formatInitializer;
    private final VerificationNumberInitializer numberInitializer;
    private final JavaMailSender mailSender;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    public void send(AuthEmailRequest request) throws NoSuchAlgorithmException, MessagingException {
        String email = request.getEmail();

        // 이메일 미입력 시 문구
        if(email == null || email.isEmpty()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_PROVIDED);
        }

        // 올바른 이메일 형식이 아닐 경우
        if(!SignUpValidator.checkValidationEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_INVALID_FORMAT);
        }

        // 이메일과 일치하는 계정이 없는 경우
        if(!checkExistEmail(email)) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL);
        }

        String verificationNumber = numberInitializer.initVerificationNumber();
        MimeMessage message = formatInitializer.initEmail(email, verificationNumber);
        String key = EmailDefaults.SENDER_EMAIL_KEY + email;

        try {
            mailSender.send(message);
            redisTemplate.opsForValue().set(key, verificationNumber, Duration.ofMinutes(3));
        } catch (MailException e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void verify(AuthCodeRequest request) {
        String email = request.getEmail();
        String code = request.getCode();

        if(code == null || code.isEmpty()) {
            throw new CustomException(ErrorCode.VERIFICATION_CODE_NOT_PROVIDED);
        }

        String key = EmailDefaults.SENDER_EMAIL_KEY + email;
        String sentCode = redisTemplate.opsForValue().get(key);
        if (sentCode == null) {
            throw new CustomException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        // SNS로 간편가입된 계정일 경우
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND_BY_EMAIL));
        if(user.getLoginFormat() != LoginFormat.LOCAL) {
            throw new CustomException(ErrorCode.EMAIL_SNS_ACCOUNT_EXISTS);
        }
        
        if(!sentCode.equals(code)) {
            throw new CustomException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }
    }

    public void reset(AuthPasswordRequest request) {
        String newPassword = request.getNewPassword();
        String checkNewPassword = request.getCheckNewPassword();

        if(!SignUpValidator.checkValidationPassword(newPassword)) {
            throw new CustomException(ErrorCode.PASSWORD_INVALID_FORMAT);
        }

        if(!newPassword.equals(checkNewPassword)) {
            throw new CustomException(ErrorCode.PASSWORD_CHANGE_OLD_MISMATCH);
        }

        userMapper.updateUserPassword(request.getEmail(), passwordEncoder.encode(newPassword));
    }

    private boolean checkExistEmail(String email) {
        return userMapper.findByEmail(email).isPresent();
    }
}
