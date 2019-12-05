package com.issuetrackinator.issuetrackinator.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.issuetrackinator.issuetrackinator.model.Comment;
import com.issuetrackinator.issuetrackinator.model.CommentDTO;
import com.issuetrackinator.issuetrackinator.model.Issue;
import com.issuetrackinator.issuetrackinator.repository.CommentRepository;
import com.issuetrackinator.issuetrackinator.repository.IssueRepository;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Api(tags = "Comment controller")
@RestController
@CrossOrigin
@RequestMapping("/api" + CommentController.ISSUE_PATH + "/{id}" + CommentController.COMMENTS_PATH)
@JsonIgnoreProperties("hibernateLazyInitializer")
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
    @ApiOperation("Get all the comments of an issue")
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
    @ApiOperation("Create a new comment in a issue")
    Comment createComment(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDto)
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
    @ApiOperation("Edit a comment of an issue")
    Comment editComment(@PathVariable Long id, @PathVariable(name = "comm-id") Long commId,
                        @RequestBody CommentDTO commentDto, @RequestHeader("api_key") String token)
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
    @ApiOperation("Delete a comment of an issue")
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
