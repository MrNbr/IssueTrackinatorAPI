package com.issuetrackinator.issuetrackinator.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.issuetrackinator.issuetrackinator.model.Issue;
import com.issuetrackinator.issuetrackinator.model.NewIssueDTO;
import com.issuetrackinator.issuetrackinator.model.IssueStatus;
import com.issuetrackinator.issuetrackinator.model.User;
import com.issuetrackinator.issuetrackinator.repository.IssueRepository;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;

@RestController
@RequestMapping("/api" + IssueController.ISSUE_PATH)
@JsonIgnoreProperties("hibernateLazyInitializer")
public class IssueController
{
    final static String ISSUE_PATH = "/issues";

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    UserRepository userRepository;
    
    private List<Issue> sortby(String sort) {
        switch (sort.toUpperCase()){
            case "ID":
                return issueRepository.findByOrderByIdAsc();
                
            case "TITLE":
                return issueRepository.findByOrderByTitleAsc();
                
            case "TYPE":
                return issueRepository.findByOrderByTypeAsc();
                
            case "PRIOR":
                return issueRepository.findByOrderByPriorityAsc();
                
            case "STATUS":
                return issueRepository.findByOrderByStatusAsc();
                
            case "VOTES":
                return issueRepository.findByOrderByVotesAsc();
                
            case "ASSIGN":
                return issueRepository.findByOrderByUserAssigneeAsc();
                
            default:
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Cannot sort by provided field");
        }
    }
    
    private List<Issue> select(List<Issue> sorted, String filter, String value, Optional<User> userOpt){
        if (filter.toUpperCase().equals("ALL")){
            return sorted;
        }
        
        List<Issue> select = new ArrayList<>();
        
        for(Issue candidate : sorted){
            switch (filter.toUpperCase()){
                case "OPEN":
                    if (candidate.getStatus().name().toUpperCase().equals("OPEN") ||
                        candidate.getStatus().name().toUpperCase().equals("NEW") ||
                        candidate.getStatus().name().toUpperCase().equals("ON_HOLD")){
                            select.add(candidate);
                        }
                    break;
                case "WATCHING":
                    User user_w = userOpt.get();
                    if (user_w.getWatchingIssues().contains(candidate)){
                        select.add(candidate);
                    }
                    break;
                    
                case "MINE":
                    User user_m = userOpt.get();
                    if (candidate.getUserCreator().getId().equals(user_m.getId())){
                        select.add(candidate);
                    }
                    break;
                    
                case "TYPE":
                    if (candidate.getType().name().toUpperCase().equals(value.toUpperCase())){
                        select.add(candidate);
                    }
                    break;
                case "PRIORITY":
                    if (candidate.getPriority().name().toUpperCase().equals(value.toUpperCase())){
                        select.add(candidate);
                    }
                    break;
                case "STATUS":
                    if (candidate.getStatus().name().toUpperCase().equals(value.toUpperCase())){
                        select.add(candidate);
                    }
                    break;
                case "VOTES":
                    if (candidate.getVotesUsers().size() == Integer.parseInt(value)){
                        select.add(candidate);
                    }
                    break;
                case "ASSIGN":
                    User asignee = candidate.getUserAsignee();
                    if (asignee != null && asignee.getId().equals(Long.parseLong(value))){
                        select.add(candidate);
                    }
                    break;
                default:
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "Unknown filter");
            }
        }
        
        return select;
    }

    // Cas base Get
    @GetMapping
    List<Issue> getAllIssues(@RequestParam(required = false, defaultValue = "all", value="filter") String filter,
                             @RequestParam(required = false, defaultValue = "id", value="sort") String sort,
                             @RequestParam(required = false, defaultValue = "DESC", value="order") String order,
                             @RequestParam(required = false, defaultValue = "id", value="value") String value,
                             /*@RequestParam(required = false, defaultValue = "0", value="page") String page,*/
                             @RequestHeader(required = false, value="api_key", defaultValue="-1") String api_key){
        
        Optional<User> userOpt = userRepository.findByToken(api_key);
        if (!userOpt.isPresent() && !filter.toUpperCase().equals("ALL") && !filter.toUpperCase().equals("OPEN")) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
            "Cannot apply provided filter");
        }
        
        List<Issue> select = sortby(sort);
        select = this.select(select, filter, value, userOpt);
        
        if (order.toUpperCase().equals("DESC")){
            Collections.reverse(select);
        }
        
        return select;
    }

    @GetMapping("/{id}")
    Issue getIssueById(@PathVariable Long id)
    {
        Optional<Issue> issue = issueRepository.findById(id);
        if (issue.isPresent())
        {
            return issue.get();
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }

    // Cas base Post
    @PostMapping
    Issue createNewIssue(@Valid @RequestBody NewIssueDTO issueDto)
    {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println(formatter.format(date));
        Issue issue = new Issue();
        issue.setCreationDate(date);
        issue.setUpdateDate(date);
        issue.setVotes(0);
        issue.setDescription(issueDto.getDescription());
        issue.setPriority(issueDto.getPriority());
        issue.setStatus(IssueStatus.NEW);
        issue.setTitle(issueDto.getTitle());
        issue.setType(issueDto.getType());
        issue.setUserCreator(userRepository.findById(issueDto.getUserCreatorId()).get());
        if (issueDto.getUserAssigneeId() != null)
        {
            issue.setUserAssignee(userRepository.findById(issueDto.getUserAssigneeId()).get());
        }
        return issueRepository.save(issue);
    }

    @DeleteMapping("/{id}")
    void deleteIssueById(@PathVariable Long id)
    {
        issueRepository.deleteById(id);
    }

    @PostMapping("/{id}/vote")
    Issue upvoteIssue(@PathVariable Long id, @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            Set<User> votes = issue.getVotesUsers();
            User user = userRepository.findByToken(token).get(); // Here find the user with token
            if (votes.contains(user))
            {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "User already voted this issue");
            }
            votes.add(user);
            issue.setVotesUsers(votes);
            issue.setVotes(votes.size());
            issueRepository.save(issue);
            return issue;
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }

    @DeleteMapping("/{id}/vote")
    Issue unvoteIssue(@PathVariable Long id, @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            Set<User> votes = issue.getVotesUsers();
            User user = userRepository.findByToken(token).get(); // Here find the user with token
            if (!votes.contains(user))
            {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "User didn't vote this issue");
            }
            votes.remove(user);
            issue.setVotesUsers(votes);
            issue.setVotes(votes.size());
            issueRepository.save(issue);
            return issue;
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }
    
    @PostMapping("/{id}/watch")
    Issue watchIssue(@PathVariable Long id, @RequestHeader(value="api_key", defaultValue="-1") String api_key)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        
        if (!issueOpt.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
        }
        
        Optional<User> userOpt = userRepository.findByToken(api_key);
        
        if (!userOpt.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
            "Action not available, unknown user");
        }
        
        User user = userOpt.get();
        Set<Issue> watchingIssues = user.getWatchingIssues();
        
        Issue issue = issueOpt.get();
        if (watchingIssues.contains(issue)) {
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE,
            "Already watching this issue.");
        }
        
        watchingIssues.add(issue);
        
        user.setWatchingIssues(watchingIssues);
        userRepository.save(user);
        
        return issue;
    }

    @DeleteMapping("/{id}/watch")
    Issue unwatchIssue(@PathVariable Long id, @RequestHeader(value="api_key", defaultValue="-1") String api_key)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        
        if (!issueOpt.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
        }
        
        Optional<User> userOpt = userRepository.findByToken(api_key);
        
        if (!userOpt.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
            "Action not available, unknown user");
        }
        
        User user = userOpt.get();
        Set<Issue> watchingIssues = user.getWatchingIssues();
        
        Issue issue = issueOpt.get();
        if (!watchingIssues.contains(issue)){
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE,
            "Cannot unwatch this issue due to is not even watched");
        }
        
        watchingIssues.remove(issue);
        
        user.setWatchingIssues(watchingIssues);
        userRepository.save(user);
    
        return issue;
    }
}
