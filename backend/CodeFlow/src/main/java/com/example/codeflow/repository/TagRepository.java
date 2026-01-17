package com.example.codeflow.repository;

import com.example.codeflow.model.Comment;
import com.example.codeflow.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    List<Tag> findByNameIn(List<String> names);
}
