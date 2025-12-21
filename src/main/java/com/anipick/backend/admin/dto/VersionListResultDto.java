package com.anipick.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class VersionListResultDto {
    private List<VersionMetadataDto> items;
}
