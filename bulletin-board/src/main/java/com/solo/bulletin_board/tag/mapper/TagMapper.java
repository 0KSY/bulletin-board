package com.solo.bulletin_board.tag.mapper;

import com.solo.bulletin_board.tag.dto.TagDto;
import com.solo.bulletin_board.tag.entity.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagDto.Response tagToTagResponseDto(Tag tag);

    List<TagDto.Response> tagsToTagResponseDtos(List<Tag> tags);
}
