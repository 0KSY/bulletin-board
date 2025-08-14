package com.solo.bulletin_board.postingLike.controller;

import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.dto.SingleResponseDto;
import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.postingLike.dto.PostingLikeDto;
import com.solo.bulletin_board.postingLike.mapper.PostingLikeMapper;
import com.solo.bulletin_board.postingLike.service.PostingLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/postingLikes")
public class PostingLikeController {

    private final PostingLikeService postingLikeService;
    private final PostingLikeMapper mapper;

    public PostingLikeController(PostingLikeService postingLikeService, PostingLikeMapper mapper) {
        this.postingLikeService = postingLikeService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postPostingLike(@RequestBody @Valid PostingLikeDto.Post postingLikePostDto,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails){

        Posting posting = postingLikeService
                .createPostingLike(mapper.postingLikePostDtoToPostingLike(postingLikePostDto), customUserDetails);

        return new ResponseEntity(new SingleResponseDto<>(mapper.postingToPostingLikeResponseDto(posting)), HttpStatus.OK);


    }
}
