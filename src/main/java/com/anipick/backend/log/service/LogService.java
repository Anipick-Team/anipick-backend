package com.anipick.backend.log.service;

import com.anipick.backend.log.domain.UserActionLog;
import com.anipick.backend.log.dto.LogCreateRequestDto;
import com.anipick.backend.log.mapper.LogMapper;
import com.anipick.backend.log.utils.UrlSafeObjectDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogMapper logMapper;

    public void createLog(String logPath) {
        UserActionLog decodedLog = UrlSafeObjectDecoder.decodeURL(logPath, UserActionLog.class);
        LogCreateRequestDto logCreateRequestDto = LogCreateRequestDto.from(decodedLog);
        logMapper.createLog(logCreateRequestDto);
    }
}
