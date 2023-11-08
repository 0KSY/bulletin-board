package com.solo.bulletinboard.posting.mapper;

import com.solo.bulletinboard.comment.entity.Comment;
import com.solo.bulletinboard.member.entity.Member;
import com.solo.bulletinboard.posting.dto.PostingDto;
import com.solo.bulletinboard.posting.entity.Posting;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostingMapper {

    // postDto -> entity
    // patchDto -> entity
    // entity -> responseDto

    default Posting postingPostDtoToPosting(PostingDto.Post postingPostDto){
        Member member = new Member();
        member.setMemberId(postingPostDto.getMemberId());

        Posting posting = new Posting();
        posting.setTitle(postingPostDto.getTitle());
        posting.setContent(postingPostDto.getContent());
        posting.setMember(member);

        return posting;

    }

    Posting postingPatchDtoToPosting(PostingDto.Patch postingPatchDto);

    default PostingDto.Response postingToPostingResponseDto(Posting posting){
        // Posting -> Response Dto

        PostingDto.Response response = PostingDto.Response.builder()
                .postingId(posting.getPostingId())
                .title(posting.getTitle())
                .content(posting.getContent())
                .createdAt(posting.getCreatedAt())
                .modifiedAt(posting.getModifiedAt())
                .build();

        PostingDto.MemberResponse memberResponse = PostingDto.MemberResponse.builder()
                .memberId(posting.getMember().getMemberId())
                .nickname(posting.getMember().getNickname())
                .build();

        response.setMemberResponse(memberResponse);

        List<Comment> comments = posting.getComments();

        List<PostingDto.ParentCommentResponse> parentCommentResponses
                = comments.stream()
                .filter(comment -> comment.getParent() == null)
                .map(comment -> PostingDto.ParentCommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .commentMemberResponse(PostingDto.CommentMemberResponse.builder()
                                .memberId(comment.getMember().getMemberId())
                                .nickname(comment.getMember().getNickname())
                                .build())
                        .childrenCommentResponses(comment.getChildren().stream()
                                .map(childComment -> PostingDto.ChildCommentResponse.builder()
                                        .commentId(childComment.getCommentId())
                                        .content(childComment.getContent())
                                        .createdAt(childComment.getCreatedAt())
                                        .modifiedAt(childComment.getModifiedAt())
                                        .commentMemberResponse(PostingDto.CommentMemberResponse.builder()
                                                .memberId(childComment.getMember().getMemberId())
                                                .nickname(childComment.getMember().getNickname())
                                                .build())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        response.setParentCommentResponses(parentCommentResponses);


        if(posting.getPostingLikes() != null){
            response.setLikeCount(posting.getPostingLikes().size());
        }
        else{
            response.setLikeCount(0);
        }

        return response;

    }

    default PostingDto.TitleResponse postingToPostingTitleResponseDto(Posting posting){
        PostingDto.TitleResponse response = PostingDto.TitleResponse.builder()
                .postingId(posting.getPostingId())
                .title(posting.getTitle())
                .createdAt(posting.getCreatedAt())
                .modifiedAt(posting.getModifiedAt())
                .build();

        PostingDto.MemberResponse memberResponse = PostingDto.MemberResponse.builder()
                .memberId(posting.getMember().getMemberId())
                .nickname(posting.getMember().getNickname())
                .build();

        response.setMemberResponse(memberResponse);

        return response;

    }

    List<PostingDto.TitleResponse> postingsToPostingTitleResponseDtos(List<Posting> postings);
}
