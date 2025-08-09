package com.solo.bulletin_board.posting.mapper;

import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.posting.dto.PostingDto;
import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.postingTag.entity.PostingTag;
import com.solo.bulletin_board.tag.entity.Tag;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PostingMapper {

    default Posting postingPostDtoToPosting(PostingDto.Post postingPostDto){

        Member member = new Member();
        member.setMemberId(postingPostDto.getMemberId());

        Posting posting = new Posting();
        posting.setTitle(postingPostDto.getTitle());
        posting.setContent(postingPostDto.getContent());
        posting.setViewCount(0);
        posting.setMember(member);

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
            posting.setContent(postingPatchDto.getTitle());
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

        PostingDto.MemberInfo memberInfo = PostingDto.MemberInfo.builder()
                .memberId(posting.getMember().getMemberId())
                .email(posting.getMember().getEmail())
                .nickname(posting.getMember().getNickname())
                .build();

        response.setMemberInfo(memberInfo);

        List<PostingDto.TagResponse> tagResponses = posting.getPostingTags().stream()
                .map(postingTag -> PostingDto.TagResponse.builder()
                        .tagId(postingTag.getTag().getTagId())
                        .tagName(postingTag.getTag().getTagName())
                        .build()
                ).collect(Collectors.toList());

        response.setTagResponses(tagResponses);


        List<PostingDto.ParentComment> parentComments
                = posting.getComments().stream()
                .filter(comment -> comment.getParent() == null)
                .map(comment -> PostingDto.ParentComment.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .createAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .memberInfo(PostingDto.MemberInfo.builder()
                                .memberId(comment.getMember().getMemberId())
                                .email(comment.getMember().getEmail())
                                .nickname(comment.getMember().getNickname())
                                .build())
                        .childComments(comment.getChildren().stream()
                                .map(childComment -> PostingDto.ChildComment.builder()
                                        .commentId(childComment.getCommentId())
                                        .parentId(childComment.getParent().getCommentId())
                                        .content(childComment.getContent())
                                        .createdAt(childComment.getCreatedAt())
                                        .modifiedAt(childComment.getModifiedAt())
                                        .memberInfo(PostingDto.MemberInfo.builder()
                                                .memberId(childComment.getMember().getMemberId())
                                                .email(childComment.getMember().getEmail())
                                                .nickname(childComment.getMember().getNickname())
                                                .build())
                                        .build()
                                ).collect(Collectors.toList())
                        ).build()
                ).collect(Collectors.toList());

        response.setParentComments(parentComments);

        return response;

    }

    List<PostingDto.Response> postingsToPostingResponseDtos(List<Posting> postings);

    default PostingDto.PostingTagResponse postingToPostingTagResponseDto(Posting posting){

        PostingDto.PostingTagResponse response = PostingDto.PostingTagResponse.builder()
                .postingId(posting.getPostingId())
                .title(posting.getTitle())
                .viewCount(posting.getViewCount())
                .postingLikeCount(posting.getPostingLikes().size())
                .createdAt(posting.getCreatedAt())
                .modifiedAt(posting.getModifiedAt())
                .memberInfo(PostingDto.MemberInfo.builder()
                        .memberId(posting.getMember().getMemberId())
                        .email(posting.getMember().getEmail())
                        .nickname(posting.getMember().getNickname())
                        .build()
                ).build();

        return response;

    }

    List<PostingDto.PostingTagResponse> postingsToPostingTagResponseDtos(List<Posting> postings);

}
