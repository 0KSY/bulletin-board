package com.solo.bulletinboard.postingLike.controller;

import com.solo.bulletinboard.dto.SingleResponseDto;
import com.solo.bulletinboard.posting.entity.Posting;
import com.solo.bulletinboard.posting.service.PostingService;
import com.solo.bulletinboard.postingLike.dto.PostingLikeDto;
import com.solo.bulletinboard.postingLike.mapper.PostingLikeMapper;
import com.solo.bulletinboard.postingLike.service.PostingLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/postingLikes")
@Validated
public class PostingLikeController {

    private final PostingLikeService postingLikeService;
    private final PostingService postingService;
    private final PostingLikeMapper mapper;

    public PostingLikeController(PostingLikeService postingLikeService,
                                 PostingService postingService,
                                 PostingLikeMapper mapper) {
        this.postingLikeService = postingLikeService;
        this.postingService = postingService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postPostingLike(@RequestBody @Valid PostingLikeDto.Post postingLikePostDto){

        postingLikeService.createPostingLike(mapper.postingLikePostDtoToPostingLike(postingLikePostDto));

        Posting findPosting = postingService.findVerifiedPosting(postingLikePostDto.getPostingId());

        return new ResponseEntity(new SingleResponseDto<>(mapper.postingToPostingLikeResponseDto(findPosting)), HttpStatus.OK);

    }
}
