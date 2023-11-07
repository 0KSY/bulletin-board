package com.solo.bulletinboard.comment.entity;

import com.solo.bulletinboard.member.entity.Member;
import com.solo.bulletinboard.posting.entity.Posting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now();

    // 1:N -> 1 -> List / N -> 엔티티담을변수

    @ManyToOne
    @JoinColumn(name = "PARENT_COMMENT_ID")
    private Comment parent;
    // children comment 입장

    // parent comment : children comment -> 1:N
    // 1 -> N해당 엔티티 담을 List 변수
    // N -> 1해당 엔티티 담을 변수

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Comment> children;
    // parent comment 입장


    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "POSTING_ID")
    private Posting posting;
}
