package com.solo.bulletin_board.posting.repository;

import com.solo.bulletin_board.posting.entity.Posting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingRepository extends JpaRepository<Posting, Long> {
}
