package com.solo.bulletin_board.postingLikeTest.controllerTest;

import com.google.gson.Gson;
import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.auth.utils.CustomAuthorityUtils;
import com.solo.bulletin_board.posting.entity.Posting;
import com.solo.bulletin_board.postingLike.dto.PostingLikeDto;
import com.solo.bulletin_board.postingLike.entity.PostingLike;
import com.solo.bulletin_board.postingLike.mapper.PostingLikeMapper;
import com.solo.bulletin_board.postingLike.service.PostingLikeService;
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

import java.util.List;

import static com.solo.bulletin_board.utils.ApiDocumentUtils.getRequestPreprocessor;
import static com.solo.bulletin_board.utils.ApiDocumentUtils.getResponsePreprocessor;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class PostingLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @Autowired
    private CustomAuthorityUtils customAuthorityUtils;
    @MockBean
    private PostingLikeService postingLikeService;
    @MockBean
    private PostingLikeMapper mapper;

    @Test
    void postPostingLikeTest() throws Exception{

        PostingLikeDto.Post postingLikePostDto = new PostingLikeDto.Post();
        postingLikePostDto.setPostingId(1L);

        String requestBody = gson.toJson(postingLikePostDto);

        PostingLikeDto.Response response = PostingLikeDto.Response.builder()
                .postingId(1L)
                .likeCount(1)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setMemberId(1L);
        customUserDetails.setRoles(List.of("USER"));
        customUserDetails.setAuthorities(customAuthorityUtils.createAuthorities(customUserDetails.getRoles()));

        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        given(mapper.postingLikePostDtoToPostingLike(Mockito.any(PostingLikeDto.Post.class))).willReturn(new PostingLike());
        given(postingLikeService.createPostingLike(Mockito.any(PostingLike.class), Mockito.any(CustomUserDetails.class)))
                .willReturn(new Posting());
        given(mapper.postingToPostingLikeResponseDto(Mockito.any(Posting.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                post("/postingLikes")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postingId").value(response.getPostingId()))
                .andExpect(jsonPath("$.data.likeCount").value(response.getLikeCount()))
                .andDo(document(
                        "post-postingLike",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("postingId").type(JsonFieldType.NUMBER).description("게시글 식별자")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.postingId").type(JsonFieldType.NUMBER).description("게시글 식별자"),
                                        fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER).description("좋아요 개수")
                                )
                        )
                ));

    }

}
