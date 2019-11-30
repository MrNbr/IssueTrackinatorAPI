package com.issuetrackinator.issuetrackinator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.issuetrackinator.issuetrackinator.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>
{

}
