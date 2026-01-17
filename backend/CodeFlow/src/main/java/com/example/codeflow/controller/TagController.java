package com.example.codeflow.controller;

import com.example.codeflow.model.Tag;
import com.example.codeflow.service.TagService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;
    // add tag
    @PostMapping
    public ResponseEntity<List<Tag>> createTags(@RequestBody List<String> names) {
        try {
            List<Tag> createdTags = tagService.createTags(names);
            return ResponseEntity.ok(createdTags);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
