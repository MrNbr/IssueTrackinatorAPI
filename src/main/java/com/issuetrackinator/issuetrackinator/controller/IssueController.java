package com.issuetrackinator.issuetrackinator.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.issuetrackinator.issuetrackinator.model.Issue;
import com.issuetrackinator.issuetrackinator.model.IssueDto;
import com.issuetrackinator.issuetrackinator.model.Watcher;
import com.issuetrackinator.issuetrackinator.model.IssueStatus;
import com.issuetrackinator.issuetrackinator.model.User;
import com.issuetrackinator.issuetrackinator.repository.IssueRepository;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;
import com.issuetrackinator.issuetrackinator.repository.WatcherRepository;
import com.issuetrackinator.issuetrackinator.model.IssueDefaultFilter;
import com.issuetrackinator.issuetrackinator.model.IssueLogedFilter;

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
    
    @Autowired
    WatcherRepository watcherRepository;

    // Cas base Get
    @GetMapping
    List<Issue> getAllIssues(@RequestParam(required = false, defaultValue = "all", value="filter") String filter,
                             @RequestParam(required = false, defaultValue = "id", value="sort") String sort,
                             @RequestParam(required = false, defaultValue = "DESC", value="order") String order,
                             @RequestParam(required = false, defaultValue = "id", value="value") String value,
                             @RequestParam(required = false, defaultValue = "0", value="page") String page),
                             @RequestHeader(value="api_key", defaultValue="-1")
    {
        List<Issue> select = issueRepository.findAll();
        Optional<User> userOpt = userRepository.findByToken(api_key);
        enum filters = userOpt.isPresent()? IssueLogedFilter : IssueDefaultFilter;
        
        
        switch(filter){
            case "open":
                break;
            case "watching":
                break;
            case "mine":
                break;
            default: // case "all":
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
    Issue createNewIssue(@Valid @RequestBody IssueDto issueDto)
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
        issue.setUserCreator(userRepository.findById(issueDto.getUserCreator()).get());
        if (issueDto.getUserAssignee() != null)
        {
            issue.setUserAssignee(userRepository.findById(issueDto.getUserAssignee()).get());
        }
        return issueRepository.save(issue);
    }

    @DeleteMapping("/{id}")
    void deleteIssueById(@PathVariable Long id)
    {
        issueRepository.deleteById(id);
    }

    @PostMapping("/{id}/vote")
    Issue upvoteIssue(@PathVariable Long id)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            Set<User> votes = issue.getVotesUsers();
            User user = userRepository.findAll().get(0); // Here find the user with token
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
    Issue unvoteIssue(@PathVariable Long id)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            Set<User> votes = issue.getVotesUsers();
            User user = userRepository.findAll().get(0); // Here find the user with token
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
        
        if (watchingIssues.contains(issue)) {
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE,
            "Already watching this issue.");
        }
        
        Issue issue = issueOpt.get();
        watchingIssues.add(issue);
        
        user.setWatchingIssues(watchIssue);
        UserRepository.save(user);
        
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
        if (!watchIssues.contains(issue)){
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE,
            "Cannot unwatch this issue due to is not even watched");
        }
        
        Issue issue = issueOpt.get();
        watchIssues.remove(issue);
        
        user.setWatchingIssues(watchIssues);
        userRepository.save(user);
    
        return issue;
    }
}
