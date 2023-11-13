package com.solo.bulletinboard.postingTag.entity;

import com.solo.bulletinboard.posting.entity.Posting;
import com.solo.bulletinboard.tag.entity.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PostingTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postingTagId;

    @ManyToOne
    @JoinColumn(name = "POSTING_ID")
    private Posting posting;

    @ManyToOne
    @JoinColumn(name = "TAG_ID")
    private Tag tag;
}
