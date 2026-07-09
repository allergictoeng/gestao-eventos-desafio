package br.com.petro.eventos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/healthcheck")
public class Healthcheck {

    @GetMapping
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok(String.format("{up: %s}", LocalDateTime.now()));
    }
}
