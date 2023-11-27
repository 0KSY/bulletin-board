package com.solo.bulletinboard.member.repository;

import com.solo.bulletinboard.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndSignupType(String email, Member.SignupType signupType);
    Optional<Member> findByNickname(String nickname);
}
