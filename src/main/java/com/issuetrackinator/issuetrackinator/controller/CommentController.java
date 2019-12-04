package com.issuetrackinator.issuetrackinator.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.issuetrackinator.issuetrackinator.model.Comment;
import com.issuetrackinator.issuetrackinator.model.CommentDto;
import com.issuetrackinator.issuetrackinator.model.Issue;
import com.issuetrackinator.issuetrackinator.repository.CommentRepository;
import com.issuetrackinator.issuetrackinator.repository.IssueRepository;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;

import io.swagger.annotations.Api;

@Api(tags = "Comments controller")
@CrossOrigin
@JsonIgnoreProperties("hibernateLazyInitializer")
@RestController
@RequestMapping("/api" + CommentController.ISSUE_PATH + "/{id}" + CommentController.COMMENTS_PATH)
public class CommentController
{
    final static String ISSUE_PATH = "/issues";

    final static String COMMENTS_PATH = "/comments";

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @GetMapping
    List<Comment> getComments(@PathVariable Long id)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            return issueOpt.get().getComments();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Comment createComment(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            List<Comment> comments = issue.getComments();
            Comment comment = new Comment();
            comment.setText(commentDto.getText());
            comment.setUserComment(userRepository.findById(commentDto.getIdUser()).get());
            comment.setCreationDate(new Date());
            comment = commentRepository.save(comment);
            comments.add(comment);
            issue.setComments(comments);
            issueRepository.save(issue);
            return comment;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }

    @PutMapping("/{comm-id}")
    Comment editComment(@PathVariable Long id, @PathVariable(name = "comm-id") Long commId,
        @RequestBody CommentDto commentDto, @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            List<Comment> comments = issue.getComments();
            Comment commentOld = comments.stream().filter(comm -> comm.getId().equals(commId))
                .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Couldn't find comment with the specified id"));
            if (commentOld.getUserComment().getUsername()
                .equals(userRepository.findByToken(token).get().getUsername()))
            {
                comments.remove(commentOld);
                commentOld.setText(commentDto.getText());
                commentOld.setCreationDate(new Date());
                comments.add(commentOld);
                issue.setComments(comments);
                issueRepository.save(issue);
                return commentOld;
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't edit a comment that it's not from your user");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }

    @DeleteMapping("/{comm-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable Long id, @PathVariable(name = "comm-id") Long commId,
        @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            List<Comment> comments = issue.getComments();
            Comment commentOld = comments.stream().filter(comm -> comm.getId().equals(commId))
                .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Couldn't find comment with the specified id"));
            if (commentOld.getUserComment().getUsername()
                .equals(userRepository.findByToken(token).get().getUsername()))
            {
                commentRepository.delete(commentOld);
                comments.remove(commentOld);
                issue.setComments(comments);
                issueRepository.save(issue);
                return;
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You can't delete a comment that it's not from your user");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }

}
