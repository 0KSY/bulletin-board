package com.solo.bulletinboard.comment.repository;

import com.solo.bulletinboard.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
