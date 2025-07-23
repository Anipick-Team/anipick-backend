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
  
    void updateUser(User user);
    void updateReviewCompletedYn(Long userId);
    void updateUserPassword(@Param("email") String email, @Param("password") String password);
    void updateUserNickname(@Param("userId") Long userId, @Param("nickname") String nickname);
    void updateUserEmail(@Param("userId") Long userId, @Param("email") String email);
    void updateUserByWithdrawal(@Param("userId") Long userId, @Param("nickname") String nickname);

    void deleteUserById(Long userId);
}
