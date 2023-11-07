package com.solo.bulletinboard.member.mapper;

import com.solo.bulletinboard.member.dto.MemberDto;
import com.solo.bulletinboard.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    // postDto -> entity
    // patchDto -> entity
    // entity -> responseDto

    Member memberPostDtoToMember(MemberDto.Post memberPostDto);

    Member memberPatchDtoToMember(MemberDto.Patch memberPatchDto);

    MemberDto.Response memberToMemberResponseDto(Member member);

    List<MemberDto.Response> membersToMemberResponseDtos(List<Member> members);


}
