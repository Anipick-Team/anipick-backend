package com.anipick.backend.person.controller;

import com.anipick.backend.common.auth.dto.CustomUserDetails;
import com.anipick.backend.common.dto.ApiResponse;
import com.anipick.backend.person.dto.PersonDetailPageDto;
import com.anipick.backend.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/{personId}")
    public ApiResponse<PersonDetailPageDto> getAnimeAndCharacterOfPerson(
        @PathVariable(value = "personId") Long personId,
        @RequestParam(value = "lastId", required = false) Long lastId,
        @RequestParam(value = "size", defaultValue = "18") int size,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getUserId();
        PersonDetailPageDto result = personService.getAnimeAndCharacter(personId, lastId, size, userId);
        return ApiResponse.success(result);
    }
}
