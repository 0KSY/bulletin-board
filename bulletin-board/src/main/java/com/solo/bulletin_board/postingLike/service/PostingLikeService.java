package com.solo.bulletin_board.postingLike.service;

import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.exception.BusinessLogicException;
import com.solo.bulletin_board.exception.ExceptionCode;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.member.service.MemberService;
import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.posting.repository.PostingRepository;
import com.solo.bulletin_board.postingLike.entity.PostingLike;
import com.solo.bulletin_board.postingLike.repository.PostingLikeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PostingLikeService {

    private final PostingLikeRepository postingLikeRepository;
    private final PostingRepository postingRepository;
    private final MemberService memberService;

    public PostingLikeService(PostingLikeRepository postingLikeRepository,
                              PostingRepository postingRepository,
                              MemberService memberService) {
        this.postingLikeRepository = postingLikeRepository;
        this.postingRepository = postingRepository;
        this.memberService = memberService;
    }

    public Posting createPostingLike(PostingLike postingLike, CustomUserDetails customUserDetails){

        Member findMember = memberService.findVerifiedMember(customUserDetails.getMemberId());

        postingLike.setMember(findMember);

        Optional<PostingLike> optionalPostingLike = postingLikeRepository.findByMemberMemberIdAndPostingPostingId(
                postingLike.getMember().getMemberId(), postingLike.getPosting().getPostingId());

        if(optionalPostingLike.isEmpty()){
            postingLikeRepository.save(postingLike);
        }
        else{
            postingLikeRepository.delete(optionalPostingLike.get());
        }

        Posting findPosting = postingRepository.findById(postingLike.getPosting().getPostingId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND));

        return findPosting;
    }
}
