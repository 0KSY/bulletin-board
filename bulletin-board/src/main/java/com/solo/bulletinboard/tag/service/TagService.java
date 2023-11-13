package com.solo.bulletinboard.tag.service;

import com.solo.bulletinboard.exception.BusinessLogicException;
import com.solo.bulletinboard.exception.ExceptionCode;
import com.solo.bulletinboard.posting.entity.Posting;
import com.solo.bulletinboard.posting.repository.PostingRepository;
import com.solo.bulletinboard.tag.entity.Tag;
import com.solo.bulletinboard.tag.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final PostingRepository postingRepository;

    public TagService(TagRepository tagRepository, PostingRepository postingRepository) {
        this.tagRepository = tagRepository;
        this.postingRepository = postingRepository;
    }

    public Tag findVerifiedTag(String tagName){
        return tagRepository.findByTagName(tagName);
    }

    public Tag findTag(String tagName){
        Tag findTag = findVerifiedTag(tagName);
        if(findTag == null){
            throw new BusinessLogicException(ExceptionCode.TAG_NOT_FOUND);
        }

        return findTag;
    }

    public Page<Tag> findTags(int page, int size){
        return tagRepository.findAll(
                PageRequest.of(page, size, Sort.by("tagId").descending()));
    }

    public Page<Posting> findPostingsByTag(Tag tag, int page, int size){

        List<Long> postingIds = tag.getPostingTags().stream()
                        .map(postingTag -> postingTag.getPosting().getPostingId()).collect(Collectors.toList());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("postingId").descending());

        Page<Posting> pagePostings = postingRepository.findByPostingIdIn(postingIds, pageRequest);

        return pagePostings;

    }
}
