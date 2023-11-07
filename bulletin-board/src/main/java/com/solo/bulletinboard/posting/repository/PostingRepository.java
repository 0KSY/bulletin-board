package com.solo.bulletinboard.posting.repository;

import com.solo.bulletinboard.posting.entity.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<Posting, Long> {
}
