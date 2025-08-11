package com.solo.bulletin_board.posting.mapper;

import com.solo.bulletin_board.comment.dto.CommentDto;
import com.solo.bulletin_board.member.dto.MemberDto;
import com.solo.bulletin_board.posting.dto.PostingDto;
import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.postingTag.entity.PostingTag;
import com.solo.bulletin_board.tag.dto.TagDto;
import com.solo.bulletin_board.tag.entity.Tag;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostingMapper {

    default Posting postingPostDtoToPosting(PostingDto.Post postingPostDto){

        Posting posting = new Posting();
        posting.setTitle(postingPostDto.getTitle());
        posting.setContent(postingPostDto.getContent());
        posting.setViewCount(0);

        if(postingPostDto.getPostingTagDtos() != null){

            List<PostingTag> postingTags = postingPostDto.getPostingTagDtos().stream()
                    .map(postingTagDto -> {
                        PostingTag postingTag = new PostingTag();
                        Tag tag = new Tag();
                        tag.setTagName(postingTagDto.getTagName());

                        postingTag.setTag(tag);
                        postingTag.setPosting(posting);

                        return postingTag;
                    }).collect(Collectors.toList());

            posting.setPostingTags(postingTags);
        }

        return posting;
    }

    default Posting postingPatchDtoToPosting(PostingDto.Patch postingPatchDto){

        Posting posting = new Posting();
        posting.setPostingId(postingPatchDto.getPostingId());

        if(postingPatchDto.getTitle() != null){
            posting.setTitle(postingPatchDto.getTitle());
        }
        if(postingPatchDto.getContent() != null){
            posting.setContent(postingPatchDto.getContent());
        }
        if(postingPatchDto.getPostingTagDtos() != null){

            List<PostingTag> postingTags = postingPatchDto.getPostingTagDtos().stream()
                    .map(postingTagDto -> {
                        PostingTag postingTag = new PostingTag();
                        Tag tag = new Tag();
                        tag.setTagName(postingTagDto.getTagName());

                        postingTag.setTag(tag);
//                        postingTag.setPosting(posting);

                        return postingTag;
                    }).collect(Collectors.toList());

            posting.setPostingTags(postingTags);

        }
        return posting;

    }

    default PostingDto.Response postingToPostingResponseDto(Posting posting){

        PostingDto.Response response = PostingDto.Response.builder()
                .postingId(posting.getPostingId())
                .title(posting.getTitle())
                .content(posting.getContent())
                .viewCount(posting.getViewCount())
                .postingLikeCount(posting.getPostingLikes().size())
                .createdAt(posting.getCreatedAt())
                .modifiedAt(posting.getModifiedAt())
                .build();

        MemberDto.MemberResponse memberResponse = MemberDto.MemberResponse.builder()
                .memberId(posting.getMember().getMemberId())
                .email(posting.getMember().getEmail())
                .nickname(posting.getMember().getNickname())
                .build();

        response.setMemberResponse(memberResponse);

        List<TagDto.TagResponse> tagResponses = posting.getPostingTags().stream()
                .map(postingTag -> TagDto.TagResponse.builder()
                        .tagId(postingTag.getTag().getTagId())
                        .tagName(postingTag.getTag().getTagName())
                        .build()
                ).collect(Collectors.toList());

        response.setTagResponses(tagResponses);


        List<CommentDto.ParentCommentResponse> parentCommentResponses
                = posting.getComments().stream()
                .filter(comment -> comment.getParent() == null)
                .map(comment -> CommentDto.ParentCommentResponse.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .memberResponse(MemberDto.MemberResponse.builder()
                                .memberId(comment.getMember().getMemberId())
                                .email(comment.getMember().getEmail())
                                .nickname(comment.getMember().getNickname())
                                .build())
                        .childCommentResponses(comment.getChildren().stream()
                                .map(childComment -> CommentDto.ChildCommentResponse.builder()
                                        .commentId(childComment.getCommentId())
                                        .parentId(childComment.getParent().getCommentId())
                                        .content(childComment.getContent())
                                        .createdAt(childComment.getCreatedAt())
                                        .modifiedAt(childComment.getModifiedAt())
                                        .memberResponse(MemberDto.MemberResponse.builder()
                                                .memberId(childComment.getMember().getMemberId())
                                                .email(childComment.getMember().getEmail())
                                                .nickname(childComment.getMember().getNickname())
                                                .build())
                                        .build()
                                ).collect(Collectors.toList())
                        ).build()
                ).collect(Collectors.toList());

        response.setParentCommentResponses(parentCommentResponses);

        return response;

    }

    default PostingDto.PostingInfoResponse postingToPostingInfoResponseDto(Posting posting){

        PostingDto.PostingInfoResponse response = PostingDto.PostingInfoResponse.builder()
                .postingId(posting.getPostingId())
                .title(posting.getTitle())
                .viewCount(posting.getViewCount())
                .postingLikeCount(posting.getPostingLikes().size())
                .createdAt(posting.getCreatedAt())
                .modifiedAt(posting.getModifiedAt())
                .memberResponse(MemberDto.MemberResponse.builder()
                        .memberId(posting.getMember().getMemberId())
                        .email(posting.getMember().getEmail())
                        .nickname(posting.getMember().getNickname())
                        .build()
                ).build();

        int parentCommentCount = (int) posting.getComments().stream()
                .filter(comment -> comment.getParent() == null)
                .count();

        int childCommentCount = posting.getComments().stream()
                .filter(comment -> comment.getParent() == null)
                .mapToInt(comment -> comment.getChildren().size())
                .sum();

        response.setCommentCount(parentCommentCount + childCommentCount);

        return response;

    }

    List<PostingDto.PostingInfoResponse> postingsToPostingInfoResponseDtos(List<Posting> postings);

}
