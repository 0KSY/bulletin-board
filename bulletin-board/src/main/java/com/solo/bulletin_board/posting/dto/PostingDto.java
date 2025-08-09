package com.solo.bulletin_board.posting.dto;

import com.solo.bulletin_board.postingTag.dto.PostingTagDto;
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
        private MemberInfo memberInfo;
        private List<TagResponse> tagResponses;
        private List<ParentComment> parentComments;
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
    public static class TagResponse{
        private long tagId;
        private String tagName;

    }


    @Getter
    @Setter
    @Builder
    public static class ParentComment{
        private long commentId;
        private String content;
        private LocalDateTime createAt;
        private LocalDateTime modifiedAt;
        private MemberInfo memberInfo;
        private List<ChildComment> childComments;

    }

    @Getter
    @Setter
    @Builder
    public static class ChildComment{
        private long commentId;
        private long parentId;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private MemberInfo memberInfo;

    }

    @Getter
    @Setter
    @Builder
    public static class PostingTagResponse{
        private long postingId;
        private String title;
        private int viewCount;
        private int postingLikeCount;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private MemberInfo memberInfo;

    }




}
