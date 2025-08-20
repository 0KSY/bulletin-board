package com.solo.bulletin_board.member.controller;

import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.dto.SingleResponseDto;
import com.solo.bulletin_board.member.dto.MemberDto;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.member.mapper.MemberMapper;
import com.solo.bulletin_board.member.service.MemberService;
import com.solo.bulletin_board.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/members")
@Validated
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper mapper;
    private static final String MEMBER_DEFAULT_URL = "/members";

    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postMember(@RequestBody @Valid MemberDto.Post memberPostDto){

        Member member = memberService.createMember(mapper.memberPostDtoToMember(memberPostDto));

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());

        return ResponseEntity.created(location).build();

    }

    @PostMapping("/image")
    public ResponseEntity postImage(@RequestPart("file") MultipartFile multipartFile,
                                    @AuthenticationPrincipal CustomUserDetails customUserDetails){

        Member member = memberService.uploadImage(multipartFile, customUserDetails.getMemberId());

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberImageResponseDto(member)), HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity patchMember(@RequestBody @Valid MemberDto.Patch memberPatchDto,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails){

        Member member = memberService.updateMember(mapper.memberPatchDtoToMember(memberPatchDto), customUserDetails.getMemberId());

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity getMember(@AuthenticationPrincipal CustomUserDetails customUserDetails){

        Member member = memberService.findMember(customUserDetails.getMemberId());

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteMember(@RequestBody @Valid MemberDto.Password passwordDto,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails){

        memberService.deleteMember(customUserDetails.getMemberId(), passwordDto.getPassword());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
