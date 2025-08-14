package com.solo.bulletin_board.tag.controller;

import com.solo.bulletin_board.dto.MultiResponseDto;
import com.solo.bulletin_board.tag.entity.Tag;
import com.solo.bulletin_board.tag.mapper.TagMapper;
import com.solo.bulletin_board.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/tags")
@Validated
public class TagController {

    private final TagService tagService;
    private final TagMapper mapper;

    public TagController(TagService tagService, TagMapper mapper) {
        this.tagService = tagService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity getTags(@RequestParam @Positive int page,
                                   @RequestParam @Positive int size){
        Page<Tag> pageTags = tagService.findTags(page-1, size);
        List<Tag> tags = pageTags.getContent();

        return new ResponseEntity(new MultiResponseDto<>(mapper.tagsToTagResponseDtos(tags), pageTags), HttpStatus.OK);

    }
}
