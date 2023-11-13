package com.solo.bulletinboard.tag.repository;

import com.solo.bulletinboard.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByTagName(String tagName);
}
