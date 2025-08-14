package com.solo.bulletin_board.posting.dto;

import com.solo.bulletin_board.comment.dto.CommentDto;
import com.solo.bulletin_board.member.dto.MemberDto;
import com.solo.bulletin_board.postingTag.dto.PostingTagDto;
import com.solo.bulletin_board.tag.dto.TagDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class PostingDto {

    @Getter
    @Setter
    public static class Post{
        @NotBlank
        private String title;
        @NotBlank
        private String content;
        @Valid
        private List<PostingTagDto> postingTagDtos;
    }

    @Getter
    @Setter
    public static class Patch{
        private long postingId;
        private String title;
        private String content;
        @Valid
        private List<PostingTagDto> postingTagDtos;
    }

    @Getter
    @Setter
    @Builder
    public static class Response{
        private long postingId;
        private String title;
        private String content;
        private int viewCount;
        private int postingLikeCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private MemberDto.MemberResponse memberResponse;
        private List<TagDto.TagResponse> tagResponses;
        private List<CommentDto.ParentCommentResponse> parentCommentResponses;
    }


    @Getter
    @Setter
    @Builder
    public static class PostingInfoResponse{
        private long postingId;
        private String title;
        private int viewCount;
        private int postingLikeCount;
        private int commentCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private MemberDto.MemberResponse memberResponse;

    }


}
