package com.solo.bulletinboard.tag.entity;

import com.solo.bulletinboard.postingTag.entity.PostingTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    @Column(nullable = false)
    private String tagName;

    @OneToMany(mappedBy = "tag")
    private List<PostingTag> postingTags;
}
