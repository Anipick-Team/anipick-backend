package com.anipick.backend.log.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class UserActionLog {
    private Action action;
    private Page page;
    private Area area;
    private DefaultDataBody dataBody;

    public static UserActionLog createClickLog(Page page, Area area, DefaultDataBody dataBody) {
        return new UserActionLog(Action.CLICK, page, area, dataBody);
    }

    public static UserActionLog createImpressionLog(Page page, Area area, DefaultDataBody dataBody) {
        return new UserActionLog(Action.IMPRESSION, page, area, dataBody);
    }

    public enum Action {
        CLICK, IMPRESSION
    }
}