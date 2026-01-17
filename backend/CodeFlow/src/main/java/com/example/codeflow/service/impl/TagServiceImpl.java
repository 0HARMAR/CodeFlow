package com.example.codeflow.service.impl;

import com.example.codeflow.model.Tag;
import com.example.codeflow.repository.TagRepository;
import com.example.codeflow.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;
    @Override
    public List<Tag> createTags(List<String> names) {
        List<Tag> result = new ArrayList<>();

        for (String name : names) {
            Tag tag = tagRepository
                    .findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name)));

            result.add(tag);
        }

        return result;
    }
}
