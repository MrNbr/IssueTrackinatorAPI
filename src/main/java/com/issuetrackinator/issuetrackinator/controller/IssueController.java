package com.issuetrackinator.issuetrackinator.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

import com.issuetrackinator.issuetrackinator.model.Issue;
import com.issuetrackinator.issuetrackinator.model.IssueDto;
import com.issuetrackinator.issuetrackinator.model.Watcher;
import com.issuetrackinator.issuetrackinator.repository.IssueRepository;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;
import com.issuetrackinator.issuetrackinator.repository.WatcherRepository;

@RestController
@RequestMapping("/api" + IssueController.ISSUE_PATH)
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
    List<Issue> getAllIssues()
    {
        return issueRepository.findAll();
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
        issue.setStatus(issueDto.getStatus());
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
            issue.setVotes(issue.getVotes() + 1);
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
            issue.setVotes(issue.getVotes() - 1);
            issueRepository.save(issue);
            return issue;
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }
    
    @PostMapping("/{id}/watch")
    Issue watchIssue(@PathVariable Long id)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        Optional<Watcher> watcherOpt = watcherRepository.findById(000);
        
        if (!issueOpt.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
        }
        
        Issue issue = issueOpt.get();
        Long issue_id = issue.getId();
        
        if (!watcherOpt.isPresent()){
            Watcher watcher = new Watcher(000);
            watcherRepository.save(watcher);
            
            watcherOpt = watcherRepository.findById(000);
            
        } else if (watcherOpt.get().isWatching(issue_id)) {
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE,
            "Already watching this issue.");
        }
        
        Watcher watcher = watcherOpt.get();
        watcher.addWatcher(id);
        return issue;
    }

    @DeleteMapping("/{id}/watch")
    Issue unwatchIssue(@PathVariable Long id)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        Optional<Watcher> watcherOpt = watcherRepository.findById(000);
        
        if (!issueOpt.isPresent()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
        }
        
        Issue issue = issueOpt.get();
        Long issue_id = issue.getId();
        
        if (!watcherOpt.isPresent() || !watcherOpt.get().isWatching(id)){
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE,
            "Cannot unwatch this issue due to is not even watched");
        }
        
        Watcher watcher = watcherOpt.get();
        watcher.removeWatcher(id);
        return issue;
    }
}
