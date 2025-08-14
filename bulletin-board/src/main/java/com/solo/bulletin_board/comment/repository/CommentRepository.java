package com.solo.bulletin_board.comment.repository;

import com.solo.bulletin_board.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
