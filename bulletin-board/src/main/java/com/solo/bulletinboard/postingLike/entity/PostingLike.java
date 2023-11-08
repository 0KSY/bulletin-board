package com.solo.bulletinboard.postingLike.entity;

import com.solo.bulletinboard.member.entity.Member;
import com.solo.bulletinboard.posting.entity.Posting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostingLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postingLikeId;

    private boolean liked = true;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "POSTING_ID")
    private Posting posting;

}
