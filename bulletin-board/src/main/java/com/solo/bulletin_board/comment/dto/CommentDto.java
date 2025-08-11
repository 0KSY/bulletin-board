package com.solo.bulletin_board.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class CommentDto {

    @Getter
    @Setter
    public static class Post{
        @NotBlank
        private String content;
        @Positive
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
        private long postingId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private MemberInfo memberInfo;
    }

    @Getter
    @Setter
    @Builder
    public static class MemberInfo{
        private long memberId;
        private String email;
        private String nickname;
    }
}
