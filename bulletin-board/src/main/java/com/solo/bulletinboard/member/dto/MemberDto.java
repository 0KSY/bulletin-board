package com.solo.bulletinboard.member.dto;

import com.solo.bulletinboard.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class MemberDto {

    @Getter
    @Setter
    public static class Post{
        private String email;
        private String password;
        private String nickname;

    }

    @Getter
    @Setter
    public static class Patch{
        private long memberId;
        private String nickname;
        private Member.MemberStatus memberStatus;
    }

    @Getter
    @Setter
    public static class Password{
        private String password;
    }

    @Getter
    @Setter
    @Builder
    public static class Response{
        private long memberId;
        private String email;
        private String nickname;
        private Member.MemberStatus memberStatus;
    }


}
