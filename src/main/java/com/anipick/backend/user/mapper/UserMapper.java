package com.anipick.backend.user.mapper;

import com.anipick.backend.user.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUserId(Long userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);

    void insertUser(User user);
    void updateUserPassword(@Param("email") String email, @Param("password") String password);
    void deleteUser(Long userId);
}
