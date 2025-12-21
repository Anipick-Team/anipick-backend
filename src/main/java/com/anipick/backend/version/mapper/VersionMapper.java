package com.anipick.backend.version.mapper;

import com.anipick.backend.admin.dto.CreateVersionRequestDto;
import com.anipick.backend.admin.dto.VersionKeyDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VersionMapper {

    Boolean existVersionByKey(VersionKeyDto versionKeyRequest);

    void createVersionOfUpdateOrNotice(CreateVersionRequestDto request);
}
