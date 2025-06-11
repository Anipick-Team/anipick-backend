package com.anipick.backend.user.mapper;

import com.anipick.backend.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findByUserId(Long userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);

    void insertUser(User user);
    void updateUser(User user);
    void updateReviewCompletedYn(Long userId);
    void deleteUser(Long userId);
}
