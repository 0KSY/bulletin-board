package com.solo.bulletin_board.postingLike.service;

import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.posting.service.PostingService;
import com.solo.bulletin_board.postingLike.entity.PostingLike;
import com.solo.bulletin_board.postingLike.repository.PostingLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PostingLikeService {

    private final PostingLikeRepository postingLikeRepository;
    private final PostingService postingService;

    public PostingLikeService(PostingLikeRepository postingLikeRepository, PostingService postingService) {
        this.postingLikeRepository = postingLikeRepository;
        this.postingService = postingService;
    }

    public Posting createPostingLike(PostingLike postingLike){

        Optional<PostingLike> optionalPostingLike = postingLikeRepository.findByMemberMemberIdAndPostingPostingId(
                postingLike.getMember().getMemberId(), postingLike.getPosting().getPostingId());

        if(optionalPostingLike.isEmpty()){
            postingLikeRepository.save(postingLike);
        }
        else{
            postingLikeRepository.delete(optionalPostingLike.get());
        }

        return postingService.findPosting(postingLike.getPosting().getPostingId());

    }
}
