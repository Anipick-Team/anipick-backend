package com.anipick.backend.admin.mapper;

import com.anipick.backend.admin.domain.Admin;
import com.anipick.backend.admin.dto.CreateVersionRequestDto;
import com.anipick.backend.admin.dto.VersionKeyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface AdminMapper {
    void insertAdminAccount(Admin account);

    Boolean existAdminAccountUsername(@Param(value = "username") String username);

    Optional<Admin> findByUsername(@Param(value = "username") String username);
}
