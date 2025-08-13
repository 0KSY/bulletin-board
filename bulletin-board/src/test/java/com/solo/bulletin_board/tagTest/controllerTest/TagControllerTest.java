package com.solo.bulletin_board.tagTest.controllerTest;

import com.solo.bulletin_board.tag.dto.TagDto;
import com.solo.bulletin_board.tag.entity.Tag;
import com.solo.bulletin_board.tag.mapper.TagMapper;
import com.solo.bulletin_board.tag.service.TagService;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.solo.bulletin_board.utils.ApiDocumentUtils.getRequestPreprocessor;
import static com.solo.bulletin_board.utils.ApiDocumentUtils.getResponsePreprocessor;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TagMapper mapper;
    @MockBean
    private TagService tagService;

    @Test
    void getTagsTest() throws Exception{
        Tag tag1 = new Tag();
        tag1.setTagId(1L);
        tag1.setTagName("태그1");

        Tag tag2 = new Tag();
        tag2.setTagId(2L);
        tag2.setTagName("태그2");

        TagDto.Response response1 = TagDto.Response.builder()
                .tagId(1L)
                .tagName("태그1")
                .build();

        TagDto.Response response2 = TagDto.Response.builder()
                .tagId(2L)
                .tagName("태그2")
                .build();

        Page<Tag> pageTags = new PageImpl<>(List.of(tag1, tag2),
                PageRequest.of(0, 5, Sort.by("tagId").descending()), 2);

        given(tagService.findTags(Mockito.any(int.class), Mockito.any(int.class))).willReturn(pageTags);
        given(mapper.tagsToTagResponseDtos(Mockito.any(List.class))).willReturn(List.of(response2, response1));

        ResultActions resultActions = mockMvc.perform(
                get("/tags")
                        .queryParam("page", "1")
                        .queryParam("size", "5")
                        .accept(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].tagId").value(response2.getTagId()))
                .andExpect(jsonPath("$.data[1].tagId").value(response1.getTagId()))
                .andExpect(jsonPath("$.pageInfo.page").value(1))
                .andExpect(jsonPath("$.pageInfo.size").value(5))
                .andDo(document(
                        "get-tags",
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
                                        fieldWithPath("data[].tagId").type(JsonFieldType.NUMBER).description("태그 식별자"),
                                        fieldWithPath("data[].tagName").type(JsonFieldType.STRING).description("태그 이름"),
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
}
