package com.solo.bulletin_board.posting.service;

import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.exception.BusinessLogicException;
import com.solo.bulletin_board.exception.ExceptionCode;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.member.service.MemberService;
import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.posting.repository.PostingRepository;
import com.solo.bulletin_board.postingTag.entity.PostingTag;
import com.solo.bulletin_board.postingTag.repository.PostingTagRepository;
import com.solo.bulletin_board.tag.entity.Tag;
import com.solo.bulletin_board.tag.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostingService {

    private final PostingRepository postingRepository;
    private final TagRepository tagRepository;
    private final PostingTagRepository postingTagRepository;
    private final MemberService memberService;

    public PostingService(PostingRepository postingRepository,
                          TagRepository tagRepository,
                          PostingTagRepository postingTagRepository,
                          MemberService memberService) {
        this.postingRepository = postingRepository;
        this.tagRepository = tagRepository;
        this.postingTagRepository = postingTagRepository;
        this.memberService = memberService;
    }

    Posting findVerifiedPosting(long postingId){
        Optional<Posting> optionalPosting = postingRepository.findById(postingId);

        Posting findPosting = optionalPosting
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POSTING_NOT_FOUND));

        return findPosting;

    }

    public Posting createPosting(Posting posting, CustomUserDetails customUserDetails){

        Member findMember = memberService.findVerifiedMember(customUserDetails.getMemberId());

        posting.setMember(findMember);

        List<PostingTag> postingTags = posting.getPostingTags();

        if(!postingTags.isEmpty()){

            for(PostingTag postingTag : postingTags){
                Optional<Tag> optionalTag = tagRepository.findByTagName(postingTag.getTag().getTagName());

                if(optionalTag.isPresent()){
                    postingTag.setTag(optionalTag.get());
                }
                else{
                    Tag savedTag = tagRepository.save(postingTag.getTag());
                    postingTag.setTag(savedTag);
                }
            }

        }

        return postingRepository.save(posting);
    }

    public Posting updatePosting(Posting posting, CustomUserDetails customUserDetails){

        Posting findPosting = findVerifiedPosting(posting.getPostingId());

        memberService.checkMemberId(findPosting.getMember().getMemberId(), customUserDetails);

        Optional.ofNullable(posting.getTitle())
                .ifPresent(title -> findPosting.setTitle(title));
        Optional.ofNullable(posting.getContent())
                .ifPresent(content -> findPosting.setContent(content));

        if(!posting.getPostingTags().isEmpty()){

            postingTagRepository.deleteAll(findPosting.getPostingTags());
            findPosting.getPostingTags().clear();

            List<PostingTag> postingTags = posting.getPostingTags().stream()
                    .map(postingTag -> {
                        Optional<Tag> optionalTag = tagRepository.findByTagName(postingTag.getTag().getTagName());

                        if(optionalTag.isPresent()){
                            postingTag.setTag(optionalTag.get());
                        }
                        else{
                            Tag savedTag = tagRepository.save(postingTag.getTag());
                            postingTag.setTag(savedTag);
                        }
                        postingTag.setPosting(findPosting);

                        return postingTag;
                    }).collect(Collectors.toList());

            findPosting.setPostingTags(postingTags);

        }

        return postingRepository.save(findPosting);
    }

    public Posting findPosting(long postingId){
        Posting findPosting = findVerifiedPosting(postingId);
        findPosting.setViewCount(findPosting.getViewCount() + 1);

        return postingRepository.save(findPosting);

    }

    public Page<Posting> findPostings(int page, int size){

        return postingRepository.findAll(
                PageRequest.of(page, size, Sort.by("postingId").descending()));

    }

    public Page<Posting> findPostingsByTagName(String tagName, int page, int size){

        Tag findTag = tagRepository.findByTagName(tagName).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.TAG_NOT_FOUND));

        List<Long> postingIds = findTag.getPostingTags().stream()
                .map(postingTag -> postingTag.getPosting().getPostingId()).collect(Collectors.toList());

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("postingId").descending());

        Page<Posting> pagePostings = postingRepository.findByPostingIdIn(postingIds, pageRequest);

        return pagePostings;

    }

    public void deletePosting(long postingId, CustomUserDetails customUserDetails){

        Posting findPosting = findVerifiedPosting(postingId);

        memberService.checkMemberId(findPosting.getMember().getMemberId(), customUserDetails);

        postingRepository.delete(findPosting);
    }
}
