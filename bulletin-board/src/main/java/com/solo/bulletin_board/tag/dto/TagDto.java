package com.solo.bulletin_board.tag.dto;

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


}
