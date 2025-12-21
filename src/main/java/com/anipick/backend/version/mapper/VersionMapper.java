package com.anipick.backend.version.mapper;

import com.anipick.backend.admin.dto.CreateVersionRequestDto;
import com.anipick.backend.admin.dto.VersionKeyDto;
import com.anipick.backend.version.domain.Version;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface VersionMapper {

    Boolean existVersionByKey(VersionKeyDto versionKeyRequest);

    void createVersionOfUpdateOrNotice(CreateVersionRequestDto request);

    List<Version> selectVersions(
            @Param(value = "platform") String platform,
            @Param(value = "type") String type
    );

    Optional<Version> findVersionById(@Param(value = "versionId") Long versionId);
}
