package com.solo.bulletin_board.commentTest.controllerTest;

import com.google.gson.Gson;
import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.auth.utils.CustomAuthorityUtils;
import com.solo.bulletin_board.comment.dto.CommentDto;
import com.solo.bulletin_board.comment.entity.Comment;
import com.solo.bulletin_board.comment.mapper.CommentMapper;
import com.solo.bulletin_board.comment.service.CommentService;
import com.solo.bulletin_board.member.dto.MemberDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @Autowired
    private CustomAuthorityUtils customAuthorityUtils;
    @MockBean
    private CommentService commentService;
    @MockBean
    private CommentMapper mapper;

    @BeforeEach
    void init(){

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setMemberId(1L);
        customUserDetails.setRoles(List.of("USER"));
        customUserDetails.setAuthorities(customAuthorityUtils.createAuthorities(customUserDetails.getRoles()));

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    @Test
    void postCommentTest() throws Exception{

        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setContent("댓글");

        CommentDto.Post commentPostDto = new CommentDto.Post();
        commentPostDto.setContent("댓글");
        commentPostDto.setPostingId(1L);
        commentPostDto.setParentId(1L);

        String requestBody = gson.toJson(commentPostDto);

        given(mapper.commentPostDtoToComment(Mockito.any(CommentDto.Post.class))).willReturn(new Comment());
        given(commentService.createComment(Mockito.any(Comment.class), Mockito.any(CustomUserDetails.class)))
                .willReturn(comment);

        ResultActions resultActions = mockMvc.perform(
                post("/comments")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/comments"))))
                .andDo(document(
                        "post-comment",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("content").description("댓글 내용"),
                                        fieldWithPath("postingId").description("게시글 식별자"),
                                        fieldWithPath("parentId").description("부모 댓글 식별자 (optional)").optional()
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("리소스 위치 URI")
                        )

                ));

    }

    @Test
    void patchCommentTest() throws Exception{

        CommentDto.Patch commentPatchDto = new CommentDto.Patch();
        commentPatchDto.setCommentId(1L);
        commentPatchDto.setContent("댓글");

        String requestBody = gson.toJson(commentPatchDto);

        CommentDto.Response response = CommentDto.Response.builder()
                .commentId(1L)
                .postingId(1L)
                .content("댓글")
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .memberResponse(MemberDto.MemberResponse.builder()
                        .memberId(1L)
                        .email("hgd@naver.com")
                        .nickname("홍길동")
                        .build()
                ).build();

        given(mapper.commentPatchDtoToComment(Mockito.any(CommentDto.Patch.class))).willReturn(new Comment());
        given(commentService.updateComment(Mockito.any(Comment.class), Mockito.any(CustomUserDetails.class)))
                .willReturn(new Comment());
        given(mapper.commentToCommentResponseDto(Mockito.any(Comment.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                patch("/comments/{comment-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commentId").value(response.getCommentId()))
                .andDo(document(
                        "patch-comment",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("comment-id").description("댓글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("commentId").type(JsonFieldType.NUMBER).description("댓글 식별자").ignored(),
                                        fieldWithPath("content").type(JsonFieldType.STRING)
                                                .description("댓글 내용 (optional) ").optional()
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.commentId").type(JsonFieldType.NUMBER).description("댓글 식별자"),
                                        fieldWithPath("data.postingId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                        fieldWithPath("data.content").type(JsonFieldType.STRING).description("댓글 내용"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간"),
                                        fieldWithPath("data.memberResponse").type(JsonFieldType.OBJECT).description("회원 정보"),
                                        fieldWithPath("data.memberResponse.memberId").type(JsonFieldType.NUMBER)
                                                .description("회원 식별자"),
                                        fieldWithPath("data.memberResponse.email").type(JsonFieldType.STRING)
                                                .description("이메일"),
                                        fieldWithPath("data.memberResponse.nickname").type(JsonFieldType.STRING)
                                                .description("닉네임")
                                )

                        )
                ));

    }


    @Test
    void deleteCommentTest() throws Exception{

        doNothing().when(commentService).deleteComment(Mockito.any(long.class), Mockito.any(CustomUserDetails.class));

        ResultActions resultActions = mockMvc.perform(
                delete("/comments/{comment-id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-comment",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        pathParameters(
                                parameterWithName("comment-id").description("댓글 식별자")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));
    }


}
