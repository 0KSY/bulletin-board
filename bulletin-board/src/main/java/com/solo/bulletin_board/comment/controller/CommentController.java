package com.solo.bulletin_board.comment.controller;

import com.solo.bulletin_board.auth.userDetailsService.CustomUserDetails;
import com.solo.bulletin_board.comment.dto.CommentDto;
import com.solo.bulletin_board.comment.entity.Comment;
import com.solo.bulletin_board.comment.mapper.CommentMapper;
import com.solo.bulletin_board.comment.service.CommentService;
import com.solo.bulletin_board.dto.SingleResponseDto;
import com.solo.bulletin_board.utils.UriCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@RequestMapping("/comments")
@Validated
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper mapper;
    private static final String COMMENT_DEFAULT_URL = "/comments";

    public CommentController(CommentService commentService, CommentMapper mapper) {
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postComment(@RequestBody @Valid CommentDto.Post commentPostDto,
                                      @AuthenticationPrincipal CustomUserDetails customUserDetails){

        Comment comment = commentService.createComment(mapper.commentPostDtoToComment(commentPostDto), customUserDetails);

        URI location = UriCreator.createUri(COMMENT_DEFAULT_URL, comment.getCommentId());

        return ResponseEntity.created(location).body(new SingleResponseDto<>(mapper.commentToCommentResponseDto(comment)));
    }

    @PatchMapping("/{comment-id}")
    public ResponseEntity patchComment(@RequestBody @Valid CommentDto.Patch commentPatchDto,
                                       @PathVariable("comment-id") @Positive long commentId,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails){

        commentPatchDto.setCommentId(commentId);
        Comment comment = commentService.updateComment(mapper.commentPatchDtoToComment(commentPatchDto), customUserDetails);

        return new ResponseEntity(new SingleResponseDto<>(mapper.commentToCommentResponseDto(comment)), HttpStatus.OK);
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity deleteComment(@PathVariable("comment-id") @Positive long commentId,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails){

        commentService.deleteComment(commentId, customUserDetails);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
