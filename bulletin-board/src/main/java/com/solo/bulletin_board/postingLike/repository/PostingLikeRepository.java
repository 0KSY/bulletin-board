package com.solo.bulletin_board.postingLike.repository;

import com.solo.bulletin_board.postingLike.entity.PostingLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostingLikeRepository extends JpaRepository<PostingLike, Long> {

    Optional<PostingLike> findByMemberMemberIdAndPostingPostingId(Long memberId, Long postingId);
}
