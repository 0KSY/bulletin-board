package com.solo.bulletin_board.posting.repository;

import com.solo.bulletin_board.posting.entity.Posting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostingRepository extends JpaRepository<Posting, Long> {

    Page<Posting> findByPostingIdIn(List<Long> postingIds, Pageable pageable);

}
