package com.example.icebreaker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class FeedController {

    @GetMapping("/api/feed")
    public ResponseEntity<List<Map<String, Object>>> getFeed() {
        // Empty list is fine for now; endpoint exists and is secured.
        return ResponseEntity.ok(Collections.emptyList());
    }
}