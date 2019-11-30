package com.issuetrackinator.issuetrackinator.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.issuetrackinator.issuetrackinator.model.Issue;

public interface IssueRepository extends JpaRepository<Issue, Long>
{
    Optional<User> findByToken(String token);
    
    List<Issue> findByOrderByIdAsc();
    List<Issue> findByOrderByIdDsc();
    
    List<Issue> findByOrderByUserAssigneeAsc();
    List<Issue> findByOrderByUserAssigneeDsc();
    
    List<Issue> findByOrderByTitleAsc();
    List<Issue> findByOrderByTitleDsc();
    
    List<Issue> findByOrderByStatusAsc();
    List<Issue> findByOrderByStatusDsc();
    
    List<Issue> findByOrderByTypeAsc();
    List<Issue> findByOrderByTypeDsc();
    
    List<Issue> findByOrderByCreationDateAsc();
    List<Issue> findByOrderByCreationDateDsc();
    
    List<Issue> findByOrderByUpdateDateAsc();
    List<Issue> findByOrderByUpdateDateDsc();
}
