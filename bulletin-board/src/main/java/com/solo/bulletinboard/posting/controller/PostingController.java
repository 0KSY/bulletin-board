package com.solo.bulletinboard.posting.controller;

import com.solo.bulletinboard.dto.MultiResponseDto;
import com.solo.bulletinboard.dto.SingleResponseDto;
import com.solo.bulletinboard.member.service.MemberService;
import com.solo.bulletinboard.posting.dto.PostingDto;
import com.solo.bulletinboard.posting.entity.Posting;
import com.solo.bulletinboard.posting.mapper.PostingMapper;
import com.solo.bulletinboard.posting.service.PostingService;
import com.solo.bulletinboard.tag.entity.Tag;
import com.solo.bulletinboard.tag.service.TagService;
import com.solo.bulletinboard.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/postings")
@Validated
public class PostingController {

    private final PostingService postingService;
    private final MemberService memberService;
    private final PostingMapper mapper;
    private static final String POSTING_DEFAULT_URL = "/postings";

    public PostingController(PostingService postingService, MemberService memberService, PostingMapper mapper) {
        this.postingService = postingService;
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postPosting(@RequestBody @Valid PostingDto.Post postingPostDto,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        long findMemberId = memberService.findMemberId(accessToken);
        postingPostDto.setMemberId(findMemberId);
        Posting posting = postingService.createPosting(mapper.postingPostDtoToPosting(postingPostDto));
        URI location = UriCreator.createUri(POSTING_DEFAULT_URL, posting.getPostingId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{posting-id}")
    public ResponseEntity patchPosting(@RequestBody @Valid  PostingDto.Patch postingPatchDto,
                                       @PathVariable("posting-id") @Positive long postingId,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        postingPatchDto.setPostingId(postingId);
        Posting posting = postingService.updatePosting(mapper.postingPatchDtoToPosting(postingPatchDto), accessToken);

        return new ResponseEntity(new SingleResponseDto<>(mapper.postingToPostingResponseDto(posting)), HttpStatus.OK);
    }

    @GetMapping("/{posting-id}")
    public ResponseEntity getPosting(@PathVariable("posting-id") @Positive long postingId){
        Posting posting = postingService.findPosting(postingId);

        return new ResponseEntity(new SingleResponseDto<>(mapper.postingToPostingResponseDto(posting)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getPostings(@RequestParam @Positive int page,
                                      @RequestParam @Positive int size){
        Page<Posting> pagePostings = postingService.findPostings(page-1, size);
        List<Posting> postings = pagePostings.getContent();

        return new ResponseEntity(new MultiResponseDto<>(
                mapper.postingsToPostingTitleResponseDtos(postings), pagePostings), HttpStatus.OK);
    }

    @DeleteMapping("/{posting-id}")
    public ResponseEntity deletePosting(@PathVariable("posting-id") @Positive long postingId,
                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){
        postingService.deletePosting(postingId, accessToken);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
