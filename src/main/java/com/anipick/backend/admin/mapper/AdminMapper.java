package com.anipick.backend.admin.mapper;

import com.anipick.backend.admin.domain.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    void insertAdminAccount(Admin account);

    Boolean existAdminAccountUsername(@Param(value = "username") String username);
}
