package com.solo.bulletinboard.tag.controller;

import com.solo.bulletinboard.dto.MultiResponseDto;
import com.solo.bulletinboard.posting.entity.Posting;
import com.solo.bulletinboard.tag.entity.Tag;
import com.solo.bulletinboard.tag.mapper.TagMapper;
import com.solo.bulletinboard.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/all")
    public ResponseEntity getTags(@RequestParam @Positive int page,
                                  @RequestParam @Positive int size){

        Page<Tag> pageTags = tagService.findTags(page-1, size);
        List<Tag> tags = pageTags.getContent();

        return new ResponseEntity(new MultiResponseDto<>(
                mapper.tagsToTagResponseDtos(tags), pageTags), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity getPostingsByTag(@RequestParam String tagName,
                                           @RequestParam @Positive int page,
                                           @RequestParam @Positive int size){

        Tag tag = tagService.findTag(tagName);

        Page<Posting> pagePostings = tagService.findPostingsByTag(tag, page-1, size);
        List<Posting> postings = pagePostings.getContent();


        return new ResponseEntity(new MultiResponseDto<>(
                mapper.postingsToPostingResponseDtos(postings), pagePostings), HttpStatus.OK);

    }


}
