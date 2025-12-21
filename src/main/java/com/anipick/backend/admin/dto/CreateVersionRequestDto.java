package com.anipick.backend.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateVersionRequestDto {
    @NotBlank
    private String platform;

    @Min(0)
    private Long major;

    @Min(0)
    private Long minor;

    @Min(0)
    private Long patch;

    @Min(0) @Max(1)
    private Long importantYn;

    @NotBlank
    private String url;

    @NotBlank
    private String type;

    @NotBlank
    private String title;

    @NotBlank
    private String updatedContent;
}
