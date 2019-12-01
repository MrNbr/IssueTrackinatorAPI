package com.issuetrackinator.issuetrackinator.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.issuetrackinator.issuetrackinator.model.Issue;
import com.issuetrackinator.issuetrackinator.model.User;

public interface IssueRepository extends JpaRepository<Issue, Long>
{
    List<Issue> findByOrderByIdAsc();
    List<Issue> findByUserAssigneeOrderByIdAsc(User user);
    List<Issue> findByUserCreatorOrderByIdAsc(User user);
    List<Issue> findByTypeOrderByIdAsc(String type);
    List<Issue> findByStatusOrderByIdAsc(String status);
    List<Issue> findByPriorityOrderByIdAsc(String prior);
    List<Issue> findByVotesOrderByIdAsc(Integer votes);
    
    List<Issue> findByOrderByTitleAsc();
    List<Issue> findByUserAssigneeOrderByTitleAsc(User user);
    List<Issue> findByUserCreatorOrderByTitleAsc(User user);
    List<Issue> findByTypeOrderByTitleAsc(String type);
    List<Issue> findByStatusOrderByTitleAsc(String status);
    List<Issue> findByPriorityOrderByTitleAsc(String prior);
    List<Issue> findByVotesOrderByTitleAsc(Integer votes);
    
    List<Issue> findByOrderByTypeAsc();
    List<Issue> findByUserAssigneeOrderByTypeAsc(User user);
    List<Issue> findByUserCreatorOrderByTypeAsc(User user);
    List<Issue> findByTypeOrderByTypeAsc(String type);
    List<Issue> findByStatusOrderByTypeAsc(String status);
    List<Issue> findByPriorityOrderByTypeAsc(String prior);
    List<Issue> findByVotesOrderByTypeAsc(Integer votes);
    
    List<Issue> findByOrderByStatusAsc();
    List<Issue> findByUserAssigneeOrderByStatusAsc(User user);
    List<Issue> findByUserCreatorOrderByStatusAsc(User user);
    List<Issue> findByTypeOrderByStatusAsc(String type);
    List<Issue> findByStatusOrderByStatusAsc(String status);
    List<Issue> findByPriorityOrderByStatusAsc(String prior);
    List<Issue> findByVotesOrderByStatusAsc(Integer votes);
    
    List<Issue> findByOrderByPriorityAsc();
    List<Issue> findByUserAssigneeOrderByPriorityAsc(User user);
    List<Issue> findByUserCreatorOrderByPriorityAsc(User user);
    List<Issue> findByTypeOrderByPriorityAsc(String type);
    List<Issue> findByStatusOrderByPriorityAsc(String status);
    List<Issue> findByPriorityOrderByPriorityAsc(String prior);
    List<Issue> findByVotesOrderByPriorityAsc(Integer votes);
    
    List<Issue> findByOrderByVotesAsc();
    List<Issue> findByUserAssigneeOrderByVotesAsc(User user);
    List<Issue> findByUserCreatorOrderByVotesAsc(User user);
    List<Issue> findByTypeOrderByVotesAsc(String type);
    List<Issue> findByStatusOrderByVotesAsc(String status);
    List<Issue> findByPriorityOrderByVotesAsc(String prior);
    List<Issue> findByVotesOrderByVotesAsc(Integer votes);
    
    List<Issue> findByOrderByUserAssigneeAsc();
    List<Issue> findByUserAssigneeOrderByUserAssigneeAsc(User user);
    List<Issue> findByUserCreatorOrderByUserAssigneeAsc(User user);
    List<Issue> findByTypeOrderByUserAssigneeAsc(String type);
    List<Issue> findByStatusOrderByUserAssigneeAsc(String status);
    List<Issue> findByPriorityOrderByUserAssigneeAsc(String prior);
    List<Issue> findByVotesOrderByUserAssigneeAsc(Integer votes);
    
    List<Issue> findByOrderByCreationDateAsc();
    List<Issue> findByUserAssigneeOrderByCreationDateAsc(User user);
    List<Issue> findByUserCreatorOrderByCreationDateAsc(User user);
    List<Issue> findByTypeOrderByCreationDateAsc(String type);
    List<Issue> findByStatusOrderByCreationDateAsc(String status);
    List<Issue> findByPriorityOrderByCreationDateAsc(String prior);
    List<Issue> findByVotesOrderByCreationDateAsc(Integer votes);
    
    List<Issue> findByOrderByUpdateDateAsc();
    List<Issue> findByUserAssigneeOrderByUpdateDateAsc(User user);
    List<Issue> findByUserCreatorOrderByUpdateDateAsc(User user);
    List<Issue> findByTypeOrderByUpdateDateAsc(String type);
    List<Issue> findByStatusOrderByUpdateDateAsc(String status);
    List<Issue> findByPriorityOrderByUpdateDateAsc(String prior);
    List<Issue> findByVotesOrderByUpdateDateAsc(Integer votes);
}
