package com.solo.bulletinboard.member.service;

import com.solo.bulletinboard.auth.jwt.JwtTokenizer;
import com.solo.bulletinboard.auth.utils.CustomAuthorityUtils;
import com.solo.bulletinboard.exception.BusinessLogicException;
import com.solo.bulletinboard.exception.ExceptionCode;
import com.solo.bulletinboard.member.entity.Member;
import com.solo.bulletinboard.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final JwtTokenizer jwtTokenizer;

    public MemberService(MemberRepository memberRepository,
                         PasswordEncoder passwordEncoder,
                         CustomAuthorityUtils customAuthorityUtils,
                         JwtTokenizer jwtTokenizer) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.customAuthorityUtils = customAuthorityUtils;
        this.jwtTokenizer = jwtTokenizer;
    }


    private void verifyExistsEmail(String email){
        Optional<Member> optionalMember = memberRepository.findByEmailAndSignupType(email, Member.SignupType.SERVER);

        if(optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }
    }

    private void verifyExistsNickname(String nickname){
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);

        if(optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NICKNAME_EXISTS);
        }
    }

    private Member findVerifiedMember(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        Member findMember = optionalMember
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return findMember;
    }

    public long findMemberId(String accessToken){
        String jws = accessToken.replace("Bearer ", "");
        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        long findMemberId = jwtTokenizer.getMemberId(jws, base64EncodedSecretKey);

        return findMemberId;

    }

    public void verifyMemberId(String accessToken, long memberId){
        long findMemberId = findMemberId(accessToken);

        if(findMemberId != memberId){
            throw new BusinessLogicException(ExceptionCode.MEMBER_ID_NOT_MATCHED);
        }
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

    public Member updateMember(Member member, String accessToken){

        Member findMember = findVerifiedMember(member.getMemberId());

        verifyMemberId(accessToken, member.getMemberId());

        Optional.ofNullable(member.getNickname())
                .ifPresent(nickName -> findMember.setNickname(nickName));
        Optional.ofNullable(member.getMemberStatus())
                .ifPresent(memberStatus -> findMember.setMemberStatus(memberStatus));

        return memberRepository.save(findMember);

    }

    public Member findMember(long memberId, String accessToken){

        Member findMember = findVerifiedMember(memberId);

        verifyMemberId(accessToken, findMember.getMemberId());

        return findMember;

    }

    public Page<Member> findMembers(int page, int size){
        return memberRepository.findAll(
                PageRequest.of(page, size, Sort.by("memberId").descending()));
    }

    public void deleteMember(long memberId, String password, String accessToken){
        Member findMember = findVerifiedMember(memberId);

        verifyMemberId(accessToken, findMember.getMemberId());

        if(!passwordEncoder.matches(password, findMember.getPassword())){
            throw new BusinessLogicException(ExceptionCode.MEMBER_PASSWORD_NOT_MATCHED);
        }

        memberRepository.delete(findMember);
    }




}
