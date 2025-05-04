package com.anipick.backend.anime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetaDataGroupResponse {
    private int code;
    private String value;
    private MetaDataGroupResult result;
}
