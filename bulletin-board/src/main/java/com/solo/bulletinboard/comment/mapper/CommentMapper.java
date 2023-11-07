package com.solo.bulletinboard.comment.mapper;

import com.solo.bulletinboard.comment.dto.CommentDto;
import com.solo.bulletinboard.comment.entity.Comment;
import com.solo.bulletinboard.member.entity.Member;
import com.solo.bulletinboard.posting.entity.Posting;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    // postDto -> entity
    // patchDto -> entity
    // entity -> response dto

    default Comment commentPostDtoToComment(CommentDto.Post commentPostDto){
        Member member = new Member();
        member.setMemberId(commentPostDto.getMemberId());

        Posting posting = new Posting();
        posting.setPostingId(commentPostDto.getPostingId());

        Comment comment = new Comment();
        comment.setContent(commentPostDto.getContent());
        comment.setMember(member);
        comment.setPosting(posting);

        if(commentPostDto.getParentId() != 0){
            Comment parentComment = new Comment();
            parentComment.setCommentId(commentPostDto.getParentId());

            comment.setParent(parentComment);
        }

        return comment;
    }

    Comment commentPatchDtoToComment(CommentDto.Patch commentPatchDto);

    default CommentDto.Response commentToCommentResponseDto(Comment comment){
        CommentDto.Response response = CommentDto.Response.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .postingId(comment.getPosting().getPostingId())
                .build();

        CommentDto.MemberResponse memberResponse = CommentDto.MemberResponse.builder()
                .memberId(comment.getMember().getMemberId())
                .nickname(comment.getMember().getNickname())
                .build();

        response.setMemberResponse(memberResponse);

        return response;
    }


}
