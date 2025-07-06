package com.anipick.backend.log.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserActionLog {
    private Action action;
    private Page page;
    private Area area;
    private DefaultDataBody dataBody;

    public static UserActionLog createClickLog(final Page page, final Area area, final DefaultDataBody dataBody) {
        return new UserActionLog(Action.CLICK, page, area, dataBody);
    }

    public static UserActionLog createImpressionLog(final Page page, final Area area, final DefaultDataBody dataBody) {
        return new UserActionLog(Action.IMPRESSION, page, area, dataBody);
    }

    private enum Action {
        CLICK, IMPRESSION
    }
}