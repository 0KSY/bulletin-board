package com.solo.bulletinboard.postingLike.service;

import com.solo.bulletinboard.postingLike.entity.PostingLike;
import com.solo.bulletinboard.postingLike.repository.PostingLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostingLikeService {
    private final PostingLikeRepository postingLikeRepository;

    public PostingLikeService(PostingLikeRepository postingLikeRepository) {
        this.postingLikeRepository = postingLikeRepository;
    }

    public void createPostingLike(PostingLike postingLike){
        PostingLike findPostingLike = postingLikeRepository.findByMemberMemberIdAndPostingPostingId(
                postingLike.getMember().getMemberId(), postingLike.getPosting().getPostingId());

        if(findPostingLike == null){
            postingLikeRepository.save(postingLike);
        }
        else{
            postingLikeRepository.delete(findPostingLike);
        }
    }
}
