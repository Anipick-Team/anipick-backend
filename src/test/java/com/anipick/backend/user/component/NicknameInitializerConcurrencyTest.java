package com.anipick.backend.user.component;

import com.anipick.backend.user.domain.LoginFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NicknameInitializerConcurrencyTest {
    @Autowired
    NicknameInitializer nicknameInitializer;

    @Test
    @DisplayName("동시에 100개를 발급하고 중복이 없거나 길이가 20자여야 한다.")
    void concurrencyTest() throws InterruptedException {
        int threads = 100;
        int perThread = 100;
        int total = threads * perThread;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);

        // 결과 수집
        Set<String> nicknames = Collections.newSetFromMap(new ConcurrentHashMap<>());
        List<Future<?>> futures = new ArrayList<>(threads);

        for (int t = 0; t < threads; t++) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    for (int i = 0; i < perThread; i++) {
                        String nickname = nicknameInitializer.generateNickname(LoginFormat.LOCAL);
                        // 길이(20자)와 중복 여부 확인
                        if (nickname.length() != 20) {
                            throw new AssertionError("length != 20: " + nickname);
                        }
                        if (!nicknames.add(nickname)) {
                            throw new AssertionError("duplicate: " + nickname);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            }));
        }

        // 동시에 시작
        ready.await();
        long begin = System.nanoTime();
        start.countDown();
        done.await();
        long elapsedMs = (System.nanoTime() - begin) / 1_000_000;

        // 정리
        pool.shutdown();

        // 최종 검증
        assertThat(nicknames).hasSize(total);
        System.out.println("Generated: " + total + " in " + elapsedMs + " ms");
    }
}