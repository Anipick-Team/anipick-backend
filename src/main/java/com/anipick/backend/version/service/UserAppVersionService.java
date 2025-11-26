package com.anipick.backend.version.service;

import com.anipick.backend.version.dto.NoticeUpdateItemDto;
import com.anipick.backend.version.dto.UserAppVersionRequestDto;
import com.anipick.backend.version.dto.VersionResultDto;
import com.anipick.backend.version.mapper.UserAppVersionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAppVersionService {

    private final UserAppVersionMapper userAppVersionMapper;

    public VersionResultDto getUserAppVersion(UserAppVersionRequestDto request) {
        Integer major = request.getMajorVersion();
        Integer minor = request.getMinorVersion();
        Integer patch = request.getPatchVersion();
        String platform = request.getPlatform();

        Boolean updateRequired = userAppVersionMapper.existsRequiredUpdate(major, minor, patch, platform);
        NoticeUpdateItemDto item = userAppVersionMapper.findVersion(platform);

        return VersionResultDto.from(updateRequired, item);
    }
}
