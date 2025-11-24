package com.anipick.backend.common.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app")
public class DeeplinkController {
    @GetMapping({"/anime/detail/**", "/**"})
    public String forwardToLanding(HttpServletRequest request) {
        return "forward:/landing.html";
    }
}
