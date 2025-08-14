package com.solo.bulletin_board.postingLike.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

public class PostingLikeDto {

    @Getter
    @Setter
    public static class Post{
        @Positive
        private long postingId;
    }

    @Getter
    @Setter
    @Builder
    public static class Response{
        private long postingId;
        private int likeCount;
    }
}
