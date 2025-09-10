package com.anipick.backend.user.component;

import com.anipick.backend.user.domain.LoginFormat;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import java.util.Base64;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class NicknameInitializer {
    private static final Base64.Encoder BASE64 = Base64.getUrlEncoder().withoutPadding();

    private volatile long lastMs = -1L;
    private int sequence = 0;

    private final int serverId = ManagementFactory.getRuntimeMXBean().getName().hashCode() & 0x3FF;

    public String generateNickname(LoginFormat loginFormat) {
        String loginFormatFirstLetter = loginFormat.toString().substring(0, 1);
        TsSequence tsSequence = nextTsSequence(System.currentTimeMillis());
        int rand32 = (int) (UUID.randomUUID().getLeastSignificantBits() & 0xFFFF_FFFFL);

        ByteBuffer buf = ByteBuffer.allocate(16).order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) tsSequence.sequence);
        buf.putInt(rand32);
        buf.putLong(tsSequence.ms);
        buf.putShort((short) serverId);
        String tail = BASE64.encodeToString(buf.array()).substring(0, 9);

        return loginFormatFirstLetter + tail;
    }

    private synchronized TsSequence nextTsSequence(long now) {
        if(now <= lastMs) {
            if(++sequence >= 1000) {
                lastMs++;
                sequence = 0;
            }
        } else {
            lastMs = now;
            sequence = 0;
        }

        return new TsSequence(lastMs, sequence);
    }

    private static final class TsSequence {
        final long ms;
        final int sequence;

        TsSequence(long ms, int sequence) {
            this.ms = ms;
            this.sequence = sequence;
        }
    }
}
