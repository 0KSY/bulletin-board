package com.solo.bulletinboard.member.controller;

import com.solo.bulletinboard.dto.MultiResponseDto;
import com.solo.bulletinboard.dto.SingleResponseDto;
import com.solo.bulletinboard.member.dto.MemberDto;
import com.solo.bulletinboard.member.entity.Member;
import com.solo.bulletinboard.member.mapper.MemberMapper;
import com.solo.bulletinboard.member.service.MemberService;
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

    @PostMapping("/signup")
    public ResponseEntity postMember(@RequestBody @Valid MemberDto.Post memberPostDto){
        Member member = memberService.createMember(mapper.memberPostDtoToMember(memberPostDto));

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());

        return ResponseEntity.created(location).build();
    }


    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@RequestBody @Valid MemberDto.Patch memberPatchDto,
                                      @PathVariable("member-id") @Positive long memberId,
                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        memberPatchDto.setMemberId(memberId);

        Member member = memberService.updateMember(mapper.memberPatchDtoToMember(memberPatchDto), accessToken);

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);

    }


    @GetMapping("/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") @Positive long memberId,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        Member member = memberService.findMember(memberId, accessToken);

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity getMembers(@RequestParam @Positive int page,
                                     @RequestParam @Positive int size){

        Page<Member> pageMembers = memberService.findMembers(page-1, size);
        List<Member> members = pageMembers.getContent();

        return new ResponseEntity(new MultiResponseDto<>(
                mapper.membersToMemberResponseDtos(members), pageMembers), HttpStatus.OK);

    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(@RequestBody @Valid MemberDto.Password passwordDto,
                                       @PathVariable("member-id") @Positive long memberId,
                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){

        memberService.deleteMember(memberId, passwordDto.getPassword(), accessToken);

        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }
}
