package com.solo.bulletinboard.postingLike.repository;

import com.solo.bulletinboard.postingLike.entity.PostingLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostingLikeRepository extends JpaRepository<PostingLike, Long> {

    PostingLike findByMemberMemberIdAndPostingPostingId(Long memberId, Long postingId);
}
