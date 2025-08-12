package com.solo.bulletin_board.auth.service;

import com.solo.bulletin_board.auth.jwt.JwtTokenizer;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthService {

    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;

    public AuthService(JwtTokenizer jwtTokenizer, MemberService memberService) {
        this.jwtTokenizer = jwtTokenizer;
        this.memberService = memberService;
    }

    public String renewAccessToken(String refreshToken){

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        long findMemberId = ((Number)jwtTokenizer.getClaims(refreshToken, base64EncodedSecretKey).getBody()
                .get("memberId")).longValue();

        Member findMember = memberService.findVerifiedMember(findMemberId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", findMember.getMemberId());
        claims.put("username", findMember.getEmail());
        claims.put("roles", findMember.getRoles());

        String subject = findMember.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        return jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);


    }


}
