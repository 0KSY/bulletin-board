package com.solo.bulletin_board.postingTest.controllerTest;

import com.google.gson.Gson;
import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.auth.utils.CustomAuthorityUtils;
import com.solo.bulletin_board.comment.dto.CommentDto;
import com.solo.bulletin_board.member.dto.MemberDto;
import com.solo.bulletin_board.posting.dto.PostingDto;
import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.posting.mapper.PostingMapper;
import com.solo.bulletin_board.posting.service.PostingService;
import com.solo.bulletin_board.postingTag.dto.PostingTagDto;
import com.solo.bulletin_board.tag.dto.TagDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.solo.bulletin_board.utils.ApiDocumentUtils.getRequestPreprocessor;
import static com.solo.bulletin_board.utils.ApiDocumentUtils.getResponsePreprocessor;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class PostingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @Autowired
    private CustomAuthorityUtils customAuthorityUtils;
    @MockBean
    private PostingService postingService;
    @MockBean
    private PostingMapper mapper;

    private Posting posting;
    private Posting posting2;
    private Page<Posting> pagePostings;
    private PostingDto.Response response;
    private PostingDto.PostingInfoResponse response1;
    private PostingDto.PostingInfoResponse response2;

    @BeforeEach
    void init(){

        posting = new Posting();
        posting.setPostingId(1L);
        posting2 = new Posting();
        posting2.setPostingId(2L);

        pagePostings = new PageImpl<>(List.of(posting, posting2),
                PageRequest.of(0, 5, Sort.by("postingId").descending()),2);

        MemberDto.MemberResponse memberResponse = MemberDto.MemberResponse.builder()
                .memberId(1L)
                .email("hgd@naver.com")
                .nickname("홍길동")
                .build();

        TagDto.TagResponse tagResponse = TagDto.TagResponse.builder()
                .tagId(1L)
                .tagName("태그")
                .build();

        CommentDto.ChildCommentResponse childCommentResponse = CommentDto.ChildCommentResponse.builder()
                .commentId(2L)
                .parentId(1L)
                .content("자식 댓글")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .memberResponse(memberResponse)
                .build();

        CommentDto.ParentCommentResponse parentCommentResponse = CommentDto.ParentCommentResponse.builder()
                .commentId(1L)
                .content("부모 댓글")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .memberResponse(memberResponse)
                .childCommentResponses(List.of(childCommentResponse))
                .build();

        response = PostingDto.Response.builder()
                .postingId(1L)
                .title("제목")
                .content("내용")
                .viewCount(0)
                .postingLikeCount(0)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .memberResponse(memberResponse)
                .tagResponses(List.of(tagResponse))
                .parentCommentResponses(List.of(parentCommentResponse))
                .build();

        response1 = PostingDto.PostingInfoResponse.builder()
                .postingId(1L)
                .title("제목")
                .viewCount(0)
                .postingLikeCount(0)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .memberResponse(memberResponse)
                .build();

        response2 = PostingDto.PostingInfoResponse.builder()
                .postingId(2L)
                .title("제목")
                .viewCount(0)
                .postingLikeCount(0)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .memberResponse(memberResponse)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setMemberId(1L);
        customUserDetails.setRoles(List.of("USER"));
        customUserDetails.setAuthorities(customAuthorityUtils.createAuthorities(customUserDetails.getRoles()));

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }


    @Test
    void postPostingTest() throws Exception{

        PostingDto.Post postingPostDto = new PostingDto.Post();
        postingPostDto.setTitle("제목");
        postingPostDto.setContent("내용");

        PostingTagDto postingTagDto = new PostingTagDto();
        postingTagDto.setTagName("태그");

        postingPostDto.setPostingTagDtos(List.of(postingTagDto));

        String requestBody = gson.toJson(postingPostDto);

        response.setParentCommentResponses(List.of());

        given(mapper.postingPostDtoToPosting(Mockito.any(PostingDto.Post.class))).willReturn(new Posting());
        given(postingService.createPosting(Mockito.any(Posting.class), Mockito.any(CustomUserDetails.class))).willReturn(posting);
        given(mapper.postingToPostingResponseDto(Mockito.any(Posting.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                post("/postings")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/postings"))))
                .andExpect(jsonPath("$.data.postingId").value(response.getPostingId()))
                .andDo(document(
                        "post-posting",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용"),
                                        fieldWithPath("postingTagDtos").type(JsonFieldType.ARRAY)
                                                .description("태그 생성 정보 (optional)").optional(),
                                        fieldWithPath("postingTagDtos[].tagName").type(JsonFieldType.STRING)
                                                .description("태그 이름")
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("리소스 위치 URI")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.postingId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),
                                        fieldWithPath("data.viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                                        fieldWithPath("data.postingLikeCount").type(JsonFieldType.NUMBER)
                                                .description("게시글 좋아요 수"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("data.memberResponse").type(JsonFieldType.OBJECT)
                                                .description("회원 정보"),
                                        fieldWithPath("data.memberResponse.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data.memberResponse.email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("data.memberResponse.nickname").type(JsonFieldType.STRING)
                                                .description("닉네임"),
                                        fieldWithPath("data.tagResponses[]").type(JsonFieldType.ARRAY)
                                                .description("태그 정보"),
                                        fieldWithPath("data.tagResponses[].tagId").type(JsonFieldType.NUMBER)
                                                .description("태그 식별자"),
                                        fieldWithPath("data.tagResponses[].tagName").type(JsonFieldType.STRING)
                                                .description("태그 이름"),
                                        fieldWithPath("data.parentCommentResponses").type(JsonFieldType.ARRAY)
                                                .description("부모 댓글 정보")

                                )
                        )
                ));

    }

    @Test
    void patchPostingTest() throws Exception{

        PostingDto.Patch postingPatchDto = new PostingDto.Patch();
        postingPatchDto.setPostingId(1L);
        postingPatchDto.setTitle("제목");
        postingPatchDto.setContent("내용");

        PostingTagDto postingTagDto = new PostingTagDto();
        postingTagDto.setTagName("태그");
        postingPatchDto.setPostingTagDtos(List.of(postingTagDto));

        String requestBody = gson.toJson(postingPatchDto);

        given(mapper.postingPatchDtoToPosting(Mockito.any(PostingDto.Patch.class))).willReturn(new Posting());
        given(postingService.updatePosting(Mockito.any(Posting.class), Mockito.any(CustomUserDetails.class)))
                .willReturn(new Posting());
        given(mapper.postingToPostingResponseDto(Mockito.any(Posting.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                patch("/postings/{posting-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postingId").value(response.getPostingId()))
                .andDo(document(
                        "patch-posting",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("posting-id").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("postingId").type(JsonFieldType.NUMBER).description("게시글 식별자").ignored(),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목").optional(),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용").optional(),
                                        fieldWithPath("postingTagDtos").type(JsonFieldType.ARRAY)
                                                .description("태그 생성 정보 (optional)").optional(),
                                        fieldWithPath("postingTagDtos[].tagName").type(JsonFieldType.STRING)
                                                .description("태그 이름")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.postingId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),
                                        fieldWithPath("data.viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                                        fieldWithPath("data.postingLikeCount").type(JsonFieldType.NUMBER)
                                                .description("게시글 좋아요 수"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("data.memberResponse").type(JsonFieldType.OBJECT).description("회원 정보"),
                                        fieldWithPath("data.memberResponse.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data.memberResponse.email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("data.memberResponse.nickname").type(JsonFieldType.STRING)
                                                .description("닉네임"),
                                        fieldWithPath("data.tagResponses").type(JsonFieldType.ARRAY)
                                                .description("태그 정보"),
                                        fieldWithPath("data.tagResponses[].tagId").type(JsonFieldType.NUMBER)
                                                .description("태그 식별자"),
                                        fieldWithPath("data.tagResponses[].tagName").type(JsonFieldType.STRING)
                                                .description("태그 이름"),
                                        fieldWithPath("data.parentCommentResponses").type(JsonFieldType.ARRAY)
                                                .description("부모 댓글 정보"),
                                        fieldWithPath("data.parentCommentResponses[].commentId").type(JsonFieldType.NUMBER)
                                                .description("부모 댓글 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].content").type(JsonFieldType.STRING)
                                                .description("댓글 내용"),
                                        fieldWithPath("data.parentCommentResponses[].createdAt").type(JsonFieldType.STRING)
                                                .description("생성 시간"),
                                        fieldWithPath("data.parentCommentResponses[].modifiedAt").type(JsonFieldType.STRING)
                                                .description("수정 시간"),
                                        fieldWithPath("data.parentCommentResponses[].memberResponse").type(JsonFieldType.OBJECT)
                                                .description("회원 정보"),
                                        fieldWithPath("data.parentCommentResponses[].memberResponse.memberId")
                                                .type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].memberResponse.email")
                                                .type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.parentCommentResponses[].memberResponse.nickname")
                                                .type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses")
                                                .type(JsonFieldType.ARRAY).description("자식 댓글 정보"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].commentId")
                                                .type(JsonFieldType.NUMBER).description("자식 댓글 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].parentId")
                                                .type(JsonFieldType.NUMBER).description("부모 댓글 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].content")
                                                .type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].createdAt")
                                                .type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].modifiedAt")
                                                .type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].memberResponse")
                                                .type(JsonFieldType.OBJECT).description("회원 정보"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].memberResponse.memberId")
                                                .type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].memberResponse.email")
                                                .type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].memberResponse.nickname")
                                                .type(JsonFieldType.STRING).description("닉네임")
                                )
                        )

                ));

    }

    @Test
    void getPostingTest() throws Exception{

        given(postingService.findPosting(Mockito.any(long.class))).willReturn(new Posting());
        given(mapper.postingToPostingResponseDto(Mockito.any(Posting.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                get("/postings/{posting-id}", 1)
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postingId").value(response.getPostingId()))
                .andDo(document(
                        "get-posting",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("posting-id").description("게시글 식별자")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.postingId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                        fieldWithPath("data.title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글 내용"),
                                        fieldWithPath("data.viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                                        fieldWithPath("data.postingLikeCount").type(JsonFieldType.NUMBER)
                                                .description("게시글 좋아요 수"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("data.memberResponse").type(JsonFieldType.OBJECT).description("회원 정보"),
                                        fieldWithPath("data.memberResponse.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data.memberResponse.email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("data.memberResponse.nickname").type(JsonFieldType.STRING)
                                                .description("닉네임"),
                                        fieldWithPath("data.tagResponses").type(JsonFieldType.ARRAY)
                                                .description("태그 정보"),
                                        fieldWithPath("data.tagResponses[].tagId").type(JsonFieldType.NUMBER)
                                                .description("태그 식별자"),
                                        fieldWithPath("data.tagResponses[].tagName").type(JsonFieldType.STRING)
                                                .description("태그 이름"),
                                        fieldWithPath("data.parentCommentResponses").type(JsonFieldType.ARRAY)
                                                .description("부모 댓글 정보"),
                                        fieldWithPath("data.parentCommentResponses[].commentId").type(JsonFieldType.NUMBER)
                                                .description("부모 댓글 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].content").type(JsonFieldType.STRING)
                                                .description("댓글 내용"),
                                        fieldWithPath("data.parentCommentResponses[].createdAt").type(JsonFieldType.STRING)
                                                .description("생성 시간"),
                                        fieldWithPath("data.parentCommentResponses[].modifiedAt").type(JsonFieldType.STRING)
                                                .description("수정 시간"),
                                        fieldWithPath("data.parentCommentResponses[].memberResponse").type(JsonFieldType.OBJECT)
                                                .description("회원 정보"),
                                        fieldWithPath("data.parentCommentResponses[].memberResponse.memberId")
                                                .type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].memberResponse.email")
                                                .type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.parentCommentResponses[].memberResponse.nickname")
                                                .type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses")
                                                .type(JsonFieldType.ARRAY).description("자식 댓글 정보"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].commentId")
                                                .type(JsonFieldType.NUMBER).description("자식 댓글 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].parentId")
                                                .type(JsonFieldType.NUMBER).description("부모 댓글 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].content")
                                                .type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].createdAt")
                                                .type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].modifiedAt")
                                                .type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].memberResponse")
                                                .type(JsonFieldType.OBJECT).description("회원 정보"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].memberResponse.memberId")
                                                .type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].memberResponse.email")
                                                .type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.parentCommentResponses[].childCommentResponses[].memberResponse.nickname")
                                                .type(JsonFieldType.STRING).description("닉네임")
                                )
                        )
                ));

    }

    @Test
    void getPostingsTest() throws Exception{

        given(postingService.findPostings(Mockito.any(int.class), Mockito.any(int.class))).willReturn(pagePostings);
        given(mapper.postingsToPostingInfoResponseDtos(Mockito.any(List.class))).willReturn(List.of(response2, response1));

        ResultActions resultActions = mockMvc.perform(
                get("/postings")
                        .queryParam("page", "1")
                        .queryParam("size", "5")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].postingId").value(response2.getPostingId()))
                .andExpect(jsonPath("$.data[1].postingId").value(response1.getPostingId()))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andDo(document(
                        "get-postings",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestParameters(
                                List.of(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 크기")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("데이터"),
                                        fieldWithPath("data[].postingId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data[].viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                                        fieldWithPath("data[].postingLikeCount").type(JsonFieldType.NUMBER)
                                                .description("게시글 좋아요 수"),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER)
                                                .description("게시글 댓글 수"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("data[].memberResponse").type(JsonFieldType.OBJECT).description("회원 정보"),
                                        fieldWithPath("data[].memberResponse.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data[].memberResponse.email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("data[].memberResponse.nickname").type(JsonFieldType.STRING)
                                                .description("닉네임"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER)
                                                .description("총 데이터 개수"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER)
                                                .description("총 페이지 개수")

                                )
                        )
                ));


    }


    @Test
    void getPostingsByTagTest() throws Exception{

        given(postingService.findPostingsByTagName(Mockito.any(String.class), Mockito.any(int.class), Mockito.any(int.class)))
                .willReturn(pagePostings);
        given(mapper.postingsToPostingInfoResponseDtos(Mockito.any(List.class))).willReturn(List.of(response2, response1));

        ResultActions resultActions = mockMvc.perform(
                get("/postings/tagNames")
                        .queryParam("tagName", "tag")
                        .queryParam("page", "1")
                        .queryParam("size", "5")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].postingId").value(response2.getPostingId()))
                .andExpect(jsonPath("$.data[1].postingId").value(response1.getPostingId()))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andDo(document(
                        "get-postingsByTag",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestParameters(
                                List.of(
                                        parameterWithName("tagName").description("태그 이름"),
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 크기")
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("데이터"),
                                        fieldWithPath("data[].postingId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("data[].viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                                        fieldWithPath("data[].postingLikeCount").type(JsonFieldType.NUMBER)
                                                .description("게시글 좋아요 수"),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER)
                                                .description("게시글 댓글 수"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("data[].memberResponse").type(JsonFieldType.OBJECT).description("회원 정보"),
                                        fieldWithPath("data[].memberResponse.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data[].memberResponse.email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("data[].memberResponse.nickname").type(JsonFieldType.STRING)
                                                .description("닉네임"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER)
                                                .description("총 데이터 개수"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER)
                                                .description("총 페이지 개수")
                                )
                        )
                ));

    }


    @Test
    void deletePostingTest() throws Exception{

        doNothing().when(postingService).deletePosting(Mockito.any(long.class), Mockito.any(CustomUserDetails.class));

        ResultActions resultActions = mockMvc.perform(
                delete("/postings/{posting-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-posting",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("posting-id").description("게시글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));

    }




}