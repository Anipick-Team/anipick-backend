package com.anipick.backend.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonItemDto {
	private Long personId;
	private String name;
	private String profileImage;
}
