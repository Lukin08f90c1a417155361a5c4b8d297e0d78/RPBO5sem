package com.example.lostfound.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/auth/csrf")
    public CsrfToken getCsrfToken(CsrfToken token) {
        // Возвращаем токен в JSON. Spring Security автоматически 
        // положит его в куку XSRF-TOKEN в ответе.
        return token;
    }
}