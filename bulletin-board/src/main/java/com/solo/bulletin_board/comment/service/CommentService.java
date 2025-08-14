package com.solo.bulletin_board.comment.service;

import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.comment.entity.Comment;
import com.solo.bulletin_board.comment.repository.CommentRepository;
import com.solo.bulletin_board.exception.BusinessLogicException;
import com.solo.bulletin_board.exception.ExceptionCode;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;

    public CommentService(CommentRepository commentRepository, MemberService memberService) {
        this.commentRepository = commentRepository;
        this.memberService = memberService;
    }

    public Comment findVerifiedComment(long commentId){
        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        Comment findComment = optionalComment.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));

        return findComment;

    }

    public Comment createComment(Comment comment, CustomUserDetails customUserDetails){

        Member findMember = memberService.findVerifiedMember(customUserDetails.getMemberId());
        comment.setMember(findMember);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Comment comment, CustomUserDetails customUserDetails){

        Comment findComment = findVerifiedComment(comment.getCommentId());

        memberService.checkMemberId(findComment.getMember().getMemberId(), customUserDetails);

        Optional.ofNullable(comment.getContent())
                .ifPresent(content -> findComment.setContent(content));

        return commentRepository.save(findComment);

    }

    public void deleteComment(long commentId, CustomUserDetails customUserDetails){
        Comment findComment = findVerifiedComment(commentId);

        memberService.checkMemberId(findComment.getMember().getMemberId(), customUserDetails);

        commentRepository.delete(findComment);
    }
}
