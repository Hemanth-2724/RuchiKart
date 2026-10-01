package com.ruchikart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok(Map.of(
            "app", "RuchiKart Food Delivery Backend",
            "status", "RUNNING",
            "version", "1.0",
            "api_endpoints", Map.of(
                "restaurants", "/api/restaurants",
                "auth_session", "/api/auth/session",
                "login", "/api/auth/login"
            )
        ));
    }
}
