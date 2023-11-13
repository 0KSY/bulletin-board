package com.solo.bulletinboard.posting.dto;

import com.solo.bulletinboard.postingTag.dto.PostingTagDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class PostingDto {

    @Getter
    @Setter
    public static class Post{
        private String title;
        private String content;
        private long memberId;
        private List<PostingTagDto> postingTagDtos;
    }

    @Getter
    @Setter
    public static class Patch{
        private long postingId;
        private String title;
        private String content;
    }

    @Getter
    @Setter
    @Builder
    public static class Response{
        private long postingId;
        private String title;
        private String content;
        private int likeCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private MemberResponse memberResponse;
        private List<TagResponse> tagResponses;
        private List<ParentCommentResponse> parentCommentResponses;
    }

    @Getter
    @Setter
    @Builder
    public static class TagResponse{
        private long tagId;
        private String tagName;
    }

    @Getter
    @Setter
    @Builder
    public static class ParentCommentResponse{
        private long commentId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private CommentMemberResponse commentMemberResponse;
        private List<ChildCommentResponse> childrenCommentResponses;
    }

    @Getter
    @Setter
    @Builder
    public static class ChildCommentResponse{
        private long commentId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private CommentMemberResponse commentMemberResponse;
    }

    @Getter
    @Setter
    @Builder
    public static class CommentMemberResponse{
        private long memberId;
        private String nickname;
    }

    @Getter
    @Setter
    @Builder
    public static class TitleResponse{
        private long postingId;
        private String title;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private MemberResponse memberResponse;
    }

    @Getter
    @Setter
    @Builder
    public static class MemberResponse{
        private long memberId;
        private String nickname;
    }
}
