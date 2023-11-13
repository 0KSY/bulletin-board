package com.solo.bulletinboard.tag.mapper;

import com.solo.bulletinboard.posting.entity.Posting;
import com.solo.bulletinboard.tag.dto.TagDto;

import com.solo.bulletinboard.tag.entity.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto.Response tagToTagResponseDto(Tag tag);

    List<TagDto.Response> tagsToTagResponseDtos(List<Tag> tags);

    default TagDto.PostingResponse postingToPostingResponseDto(Posting posting){

        TagDto.PostingResponse response = TagDto.PostingResponse.builder()
                .postingId(posting.getPostingId())
                .title(posting.getTitle())
                .memberId(posting.getMember().getMemberId())
                .nickname(posting.getMember().getNickname())
                .build();

        return response;
    }

    List<TagDto.PostingResponse> postingsToPostingResponseDtos(List<Posting> postings);



}
