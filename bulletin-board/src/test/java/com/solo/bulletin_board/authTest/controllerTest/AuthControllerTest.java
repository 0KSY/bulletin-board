package com.solo.bulletin_board.authTest.controllerTest;

import com.solo.bulletin_board.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.solo.bulletin_board.utils.ApiDocumentUtils.getRequestPreprocessor;
import static com.solo.bulletin_board.utils.ApiDocumentUtils.getResponsePreprocessor;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;

    @Test
    void refreshAccessTokenTest() throws Exception{

        given(authService.renewAccessToken(Mockito.any(String.class))).willReturn("accessToken");

        ResultActions resultActions = mockMvc.perform(
                post("/auth/refresh")
                        .header("Refresh", "refreshToken")
        );

        resultActions.andExpect(status().isOk())
                .andExpect(header().string("Authorization", is(startsWith("Bearer"))))
                .andDo(document(
                        "refresh-accessToken",
                        getRequestPreprocessor(),
                        getResponsePreprocessor(),
                        requestHeaders(
                                headerWithName("Refresh").description("Refresh Token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Access Token")
                        )
                ));
    }
}
