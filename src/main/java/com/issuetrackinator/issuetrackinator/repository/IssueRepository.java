package com.issuetrackinator.issuetrackinator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.issuetrackinator.issuetrackinator.model.Issue;

public interface IssueRepository extends JpaRepository<Issue, Long>
{

}
