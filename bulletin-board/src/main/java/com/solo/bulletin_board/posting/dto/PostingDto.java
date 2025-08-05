package com.solo.bulletin_board.posting.dto;

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
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        private MemberInfo memberInfo;
        private List<CommentResponse> commentResponses;
    }

    @Getter
    @Setter
    @Builder
    public static class MemberInfo{
        private long memberId;
        private String email;
        private String nickname;
    }

    @Getter
    @Setter
    @Builder
    public static class CommentResponse{
        private long commentId;
        private long postingId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private MemberInfo memberInfo;
    }
}
