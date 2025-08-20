package com.solo.bulletin_board.member.entity;

import com.solo.bulletin_board.audit.Auditable;
import com.solo.bulletin_board.comment.entity.Comment;
import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.postingLike.entity.PostingLike;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    private String image;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.MEMBER_ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private SignupType signupType;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Posting> postings = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<PostingLike> postingLikes = new ArrayList<>();

    public enum MemberStatus{
        MEMBER_ACTIVE("활동 중"),
        MEMBER_SLEEP("휴면 상태"),
        MEMBER_QUIT("회원 탈퇴");

        @Getter
        private String status;

        MemberStatus(String status) {
            this.status = status;
        }
    }

    public enum SignupType{
        SERVER,
        GOOGLE_OAUTH2;
    }
}
