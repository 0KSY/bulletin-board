package com.solo.bulletin_board.member.service;

import com.solo.bulletin_board.auth.utils.CustomAuthorityUtils;
import com.solo.bulletin_board.exception.BusinessLogicException;
import com.solo.bulletin_board.exception.ExceptionCode;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository,
                         CustomAuthorityUtils customAuthorityUtils,
                         PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.customAuthorityUtils = customAuthorityUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public void verifyExistsEmail(String email){
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if(optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    public void verifyExistsNickname(String nickname){
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);

        if(optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NICKNAME_EXISTS);
        }
    }

    public Member findVerifiedMember(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        Member findMember = optionalMember
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return findMember;

    }



    public Member createMember(Member member){

        verifyExistsEmail(member.getEmail());
        verifyExistsNickname(member.getNickname());

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        List<String> roles = customAuthorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        return memberRepository.save(member);
    }

    public Member updateMember(Member member){

        Member findMember = findVerifiedMember(member.getMemberId());

        Optional.ofNullable(member.getNickname())
                .ifPresent(nickname -> {
                    if(!nickname.equals(findMember.getNickname())){
                        verifyExistsNickname(nickname);
                    }
                    findMember.setNickname(nickname);
                });

        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(memberStatus -> findMember.setMemberStatus(memberStatus));

        return memberRepository.save(findMember);

    }

    public Member findMember(long memberId){

        Member findMember = findVerifiedMember(memberId);

        return findMember;
    }

    public void deleteMember(long memberId){

        Member findMember = findVerifiedMember(memberId);

        memberRepository.delete(findMember);
    }


}
