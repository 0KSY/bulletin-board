package com.solo.bulletinboard.tag.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class TagDto {

    @Getter
    @Setter
    @Builder
    public static class Response{
        private long tagId;
        private String tagName;
    }

    @Getter
    @Setter
    @Builder
    public static class PostingResponse{
        private long postingId;
        private String title;
        private long memberId;
        private String nickname;
    }

}
