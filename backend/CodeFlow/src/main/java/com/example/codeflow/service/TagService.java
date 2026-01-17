package com.example.codeflow.service;

import com.example.codeflow.model.Tag;

import java.util.List;

public interface TagService {
    List<Tag> createTags(List<String> names);
}
