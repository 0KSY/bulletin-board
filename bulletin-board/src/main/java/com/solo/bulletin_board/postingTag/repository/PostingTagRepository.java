package com.solo.bulletin_board.postingTag.repository;

import com.solo.bulletin_board.postingTag.entity.PostingTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingTagRepository extends JpaRepository<PostingTag, Long> {
}
