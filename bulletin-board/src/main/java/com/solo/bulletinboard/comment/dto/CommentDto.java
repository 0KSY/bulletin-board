package com.solo.bulletinboard.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @Setter
    public static class Post{
        private String content;
        private long memberId;
        private long postingId;
        private long parentId;
    }

    @Getter
    @Setter
    public static class Patch{
        private long commentId;
        private String content;
    }

    @Getter
    @Setter
    @Builder
    public static class Response{
        private long commentId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private long postingId;
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
