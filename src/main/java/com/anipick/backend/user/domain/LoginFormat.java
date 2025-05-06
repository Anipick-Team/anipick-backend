package com.anipick.backend.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginFormat {
    LOCAL, KAKAO, GOOGLE, APPLE
}
