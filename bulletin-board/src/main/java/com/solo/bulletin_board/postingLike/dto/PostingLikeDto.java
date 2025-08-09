package com.solo.bulletin_board.postingLike.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class PostingLikeDto {

    @Getter
    @Setter
    public static class Post{
        
        private long memberId;
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
