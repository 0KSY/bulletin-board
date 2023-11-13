package com.solo.bulletinboard.posting.service;

import com.solo.bulletinboard.exception.BusinessLogicException;
import com.solo.bulletinboard.exception.ExceptionCode;
import com.solo.bulletinboard.member.service.MemberService;
import com.solo.bulletinboard.posting.entity.Posting;
import com.solo.bulletinboard.posting.repository.PostingRepository;
import com.solo.bulletinboard.postingTag.entity.PostingTag;
import com.solo.bulletinboard.tag.entity.Tag;
import com.solo.bulletinboard.tag.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class PostingService {

    private final PostingRepository postingRepository;
    private final TagRepository tagRepository;
    private final MemberService memberService;

    public PostingService(PostingRepository postingRepository, TagRepository tagRepository, MemberService memberService) {
        this.postingRepository = postingRepository;
        this.tagRepository = tagRepository;
        this.memberService = memberService;
    }

    public Posting findVerifiedPosting(long postingId){
        Optional<Posting> optionalPosting = postingRepository.findById(postingId);

        Posting findPosting = optionalPosting
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND));

        return findPosting;
    }

    public Posting createPosting(Posting posting){

        for(PostingTag postingTag : posting.getPostingTags()){
            Tag findTag = tagRepository.findByTagName(postingTag.getTag().getTagName());

            if(findTag != null){
                postingTag.setTag(findTag);
            }
            else{
                Tag savedTag = tagRepository.save(postingTag.getTag());
                postingTag.setTag(savedTag);
            }
        }

        return postingRepository.save(posting);
    }

    public Posting updatePosting(Posting posting, String accessToken){
        Posting findPosting = findVerifiedPosting(posting.getPostingId());

        memberService.verifyMemberId(accessToken, findPosting.getMember().getMemberId());

        Optional.ofNullable(posting.getTitle())
                .ifPresent(title -> findPosting.setTitle(title));
        Optional.ofNullable(posting.getContent())
                .ifPresent(content -> findPosting.setContent(content));

        findPosting.setModifiedAt(LocalDateTime.now());

        return postingRepository.save(findPosting);
    }

    public Posting findPosting(long postingId){

        return findVerifiedPosting(postingId);

    }

    public Page<Posting> findPostings(int page, int size){
        return postingRepository.findAll(
                PageRequest.of(page, size, Sort.by("postingId").descending()));
    }

    public void deletePosting(long postingId, String accessToken){

        Posting findPosting = findVerifiedPosting(postingId);

        memberService.verifyMemberId(accessToken, findPosting.getMember().getMemberId());

        postingRepository.delete(findPosting);
    }
}
