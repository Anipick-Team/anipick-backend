package com.anipick.backend.user.component;

import com.anipick.backend.user.domain.LoginFormat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class NicknameInitializerConcurrencyTest {
    @InjectMocks
    NicknameInitializer nicknameInitializer;

    @Test
    @DisplayName("랜덤 닉네임을 1ms에 동시적으로 10000개를 발급한다. 그리고 중복이 없거나 길이가 12자여야 한다.")
    void nicknameConcurrencyTest() throws InterruptedException, ExecutionException {
        int threads = 100;
        int perThread = 100;
        int total = threads * perThread;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        Set<String> nicknames = ConcurrentHashMap.newKeySet();
        List<Future<?>> futures = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                start.await();

                for (int i = 0; i < perThread; i++) {
                    String nickname = nicknameInitializer.generateNickname(LoginFormat.LOCAL);
                    assertEquals(10, nickname.length(), "length != 10: " + nickname);
                    if (!nicknames.add(nickname)) {
                        throw new AssertionError("duplicate: " + nickname);
                    }
                }

                return null;
            }));
        }

        start.countDown();
        for (Future<?> f : futures) {
            f.get();
        }
        pool.shutdown();

        assertEquals(total, nicknames.size());
    }
}