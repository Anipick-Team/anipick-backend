package com.anipick.backend.log.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class LogCreateRequestDto {
    private String action;
    private String page;
    private String area;
    private String type;
    private String content;
    private int position;
    private String query;
}
