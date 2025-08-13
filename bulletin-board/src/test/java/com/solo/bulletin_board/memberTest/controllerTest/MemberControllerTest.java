package com.solo.bulletin_board.memberTest.controllerTest;

import com.google.gson.Gson;
import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.auth.utils.CustomAuthorityUtils;
import com.solo.bulletin_board.member.dto.MemberDto;
import com.solo.bulletin_board.member.entity.Member;
import com.solo.bulletin_board.member.mapper.MemberMapper;
import com.solo.bulletin_board.member.service.MemberService;
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
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Gson gson;
    @Autowired
    private CustomAuthorityUtils customAuthorityUtils;
    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberMapper mapper;

    private MemberDto.Response response;

    @BeforeEach
    void init(){

        response = MemberDto.Response.builder()
                .memberId(1L)
                .email("hgd@naver.com")
                .nickname("홍길동")
                .memberStatus(Member.MemberStatus.MEMBER_ACTIVE)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setMemberId(1L);
        customUserDetails.setRoles(List.of("USER"));
        customUserDetails.setAuthorities(customAuthorityUtils.createAuthorities(customUserDetails.getRoles()));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void postMemberTest() throws Exception{

        Member member = new Member();
        member.setMemberId(1L);
        member.setEmail("hgd@naver.com");
        member.setPassword("1234");
        member.setNickname("홍길동");

        MemberDto.Post memberPostDto = new MemberDto.Post();
        memberPostDto.setEmail("hgd@naver.com");
        memberPostDto.setPassword("1234");
        memberPostDto.setNickname("홍길동");

        String requestBody = gson.toJson(memberPostDto);

        given(mapper.memberPostDtoToMember(Mockito.any(MemberDto.Post.class))).willReturn(new Member());
        given(memberService.createMember(Mockito.any(Member.class))).willReturn(member);

        ResultActions resultActions = mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(header().string("Location", is(startsWith("/members"))))
                .andDo(document(
                        "post-member",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestFields(
                                List.of(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                                )
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("리소스 위치 URI")
                        )
                ));

    }

    @Test
    void patchMemberTest() throws Exception{

        MemberDto.Patch memberPatchDto = new MemberDto.Patch();
        memberPatchDto.setNickname("홍길동");
        memberPatchDto.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);

        String requestBody = gson.toJson(memberPatchDto);

        given(mapper.memberPatchDtoToMember(Mockito.any(MemberDto.Patch.class))).willReturn(new Member());
        given(memberService.updateMember(Mockito.any(Member.class))).willReturn(new Member());
        given(mapper.memberToMemberResponseDto(Mockito.any(Member.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                patch("/members")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(response.getMemberId()))
                .andExpect(jsonPath("$.data.email").value(response.getEmail()))
                .andExpect(jsonPath("$.data.nickname").value(response.getNickname()))
                .andDo(document(
                        "patch-member",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자").ignored(),
                                        fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임").optional(),
                                        fieldWithPath("memberStatus").type(JsonFieldType.STRING)
                                                .description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT").optional()
                                )
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING)
                                                .description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                                )
                        )
                ));

    }

    @Test
    void getMemberTest() throws Exception{

        given(memberService.findMember(Mockito.any(long.class))).willReturn(new Member());
        given(mapper.memberToMemberResponseDto(Mockito.any(Member.class))).willReturn(response);

        ResultActions resultActions = mockMvc.perform(
                get("/members")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(response.getMemberId()))
                .andDo(document("get-member",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터"),
                                        fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("data.memberStatus").type(JsonFieldType.STRING)
                                                .description("회원 상태 : MEMBER_ACTIVE / MEMBER_SLEEP / MEMBER_QUIT"),
                                        fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간"),
                                        fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description("수정 시간")
                                )
                        )
                ));

    }

    @Test
    void deleteMemberTest() throws Exception{

        MemberDto.Password passwordDto = new MemberDto.Password();
        passwordDto.setPassword("1234");

        String requestBody = gson.toJson(passwordDto);

        doNothing().when(memberService).deleteMember(Mockito.any(long.class), Mockito.any(String.class));

        ResultActions resultActions = mockMvc.perform(
                delete("/members")
                        .header(HttpHeaders.AUTHORIZATION, "accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(document(
                        "delete-member",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        ),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        )
                ));

    }


}
