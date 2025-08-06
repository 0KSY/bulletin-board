package com.solo.bulletin_board.posting.mapper;

import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.posting.dto.PostingDto;
import com.solo.bulletin_board.posting.entity.Posting;
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
        posting.setMember(member);

        return posting;
    }

    Posting postingPatchDtoToPosting(PostingDto.Patch postingPatchDto);

    default PostingDto.Response postingToPostingResponseDto(Posting posting){

        PostingDto.Response response = PostingDto.Response.builder()
                .postingId(posting.getPostingId())
                .title(posting.getTitle())
                .content(posting.getContent())
                .createdAt(posting.getCreatedAt())
                .modifiedAt(posting.getModifiedAt())
                .build();

        PostingDto.MemberInfo memberInfo = PostingDto.MemberInfo.builder()
                .memberId(posting.getMember().getMemberId())
                .email(posting.getMember().getEmail())
                .nickname(posting.getMember().getNickname())
                .build();

        response.setMemberInfo(memberInfo);

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

}
