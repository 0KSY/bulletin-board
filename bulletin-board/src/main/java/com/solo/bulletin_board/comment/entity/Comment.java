package com.solo.bulletin_board.comment.entity;

import com.solo.bulletin_board.audit.Auditable;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.posting.entity.Posting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "POSTING_ID")
    private Posting posting;
}
