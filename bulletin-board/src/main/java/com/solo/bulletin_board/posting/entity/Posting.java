package com.solo.bulletin_board.posting.entity;

import com.solo.bulletin_board.audit.Auditable;
import com.solo.bulletin_board.comment.entity.Comment;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.postingTag.entity.PostingTag;
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
public class Posting extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postingId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "posting", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "posting", cascade = CascadeType.ALL)
    private List<PostingTag> postingTags = new ArrayList<>();
}
