package com.solo.bulletin_board.member.controller;

import com.solo.bulletin_board.dto.SingleResponseDto;
import com.solo.bulletin_board.member.dto.MemberDto;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.member.mapper.MemberMapper;
import com.solo.bulletin_board.member.service.MemberService;
import com.solo.bulletin_board.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
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

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@RequestBody @Valid MemberDto.Patch memberPatchDto,
                                      @PathVariable("member-id") @Positive long memberId){

        memberPatchDto.setMemberId(memberId);

        Member member = memberService.updateMember(mapper.memberPatchDtoToMember(memberPatchDto));

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);

    }

    @GetMapping("/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") @Positive long memberId){

        Member member = memberService.findMember(memberId);

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);
    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") @Positive long memberId,
                                       @RequestBody @Valid MemberDto.Password passwordDto){

        memberService.deleteMember(memberId, passwordDto.getPassword());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
