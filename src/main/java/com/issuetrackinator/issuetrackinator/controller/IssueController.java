package com.issuetrackinator.issuetrackinator.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.issuetrackinator.issuetrackinator.model.*;
import com.issuetrackinator.issuetrackinator.repository.CommentRepository;
import com.issuetrackinator.issuetrackinator.repository.IssueRepository;
import com.issuetrackinator.issuetrackinator.repository.UploadedFileRepository;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.*;

@Api(tags = "Issue controller")
@RestController
@CrossOrigin
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
    CommentRepository commentRepository;

    @Autowired
    UploadedFileRepository uploadedFileRepository;

    private List<Issue> sortby(String sort)
    {
        switch (sort.toUpperCase())
        {
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

    private List<Issue> select(List<Issue> sorted, String filter, String value,
        Optional<User> userOpt)
    {
        if (filter.toUpperCase().equals("ALL"))
        {
            return sorted;
        }

        List<Issue> select = new ArrayList<>();

        for (Issue candidate : sorted)
        {
            switch (filter.toUpperCase())
            {
                case "OPEN":
                    if (candidate.getStatus().name().toUpperCase().equals("OPEN")
                        || candidate.getStatus().name().toUpperCase().equals("NEW")
                        || candidate.getStatus().name().toUpperCase().equals("ON_HOLD"))
                    {
                        select.add(candidate);
                    }
                    break;
                case "WATCHING":
                    User user_w = userOpt.get();
                    if (user_w.getWatchingIssues().contains(candidate))
                    {
                        select.add(candidate);
                    }
                    break;

                case "MINE":
                    User user_m = userOpt.get();
                    if (candidate.getUserCreator().getId().equals(user_m.getId()))
                    {
                        select.add(candidate);
                    }
                    break;

                case "TYPE":
                    if (candidate.getType().name().toUpperCase().equals(value.toUpperCase()))
                    {
                        select.add(candidate);
                    }
                    break;
                case "PRIORITY":
                    if (candidate.getPriority().name().toUpperCase().equals(value.toUpperCase()))
                    {
                        select.add(candidate);
                    }
                    break;
                case "STATUS":
                    if (candidate.getStatus().name().toUpperCase().equals(value.toUpperCase()))
                    {
                        select.add(candidate);
                    }
                    break;
                case "VOTES":
                    if (candidate.getVotesUsers().size() == Integer.parseInt(value))
                    {
                        select.add(candidate);
                    }
                    break;
                case "ASSIGN":
                    User assignee = candidate.getUserAssignee();
                    if (assignee != null && assignee.getId().equals(Long.parseLong(value))){
                        select.add(candidate);
                    }
                    break;
                default:
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Unknown filter");
            }
        }

        return select;
    }

    @GetMapping
    @ApiOperation("Get all the issues with the specified filter")
    List<Issue> getAllIssues(
        @RequestParam(required = false, defaultValue = "all", value = "filter") String filter,
        @RequestParam(required = false, defaultValue = "id", value = "sort") String sort,
        @RequestParam(required = false, defaultValue = "DESC", value = "order") String order,
        @RequestParam(required = false, defaultValue = "id", value = "value") String value,
        /* @RequestParam(required = false, defaultValue = "0", value="page") String page, */
        @RequestHeader(required = false, value = "api_key") String api_key)
    {

        Optional<User> userOpt = Optional.empty();

        if (api_key != null){
            userOpt = userRepository.findByToken(api_key);
        }
        
        if (!userOpt.isPresent() && (filter.toUpperCase().equals("MINE")
            || filter.toUpperCase().equals("WATCHING")))
        {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                "Cannot apply provided filter");
        }

        List<Issue> select = sortby(sort);
        select = this.select(select, filter, value, userOpt);

        if (order.toUpperCase().equals("DESC"))
        {
            Collections.reverse(select);
        }

        return select;
    }

    @GetMapping("/{id}")
    @ApiOperation("Get an issue by the id")
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

    @PostMapping
    @ApiOperation("Create a new issue")
    Issue createNewIssue(@Valid @RequestBody NewIssueDTO issueDto)
    {
        Date date = new Date();
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

    // just to change description, priority, title and type. Status has its own path
    @PutMapping("/{id}")
    Issue editIssue(@PathVariable Long id, @Valid @RequestBody IssueDto issueDto,
        @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            if (issue.getUserCreator().equals(userRepository.findByToken(token).get()))
            {
                Date date = new Date();
                issue.setUpdateDate(date);
                issue.setDescription(issueDto.getDescription());
                issue.setPriority(issueDto.getPriority());
                issue.setTitle(issueDto.getTitle());
                issue.setUserAssignee(userRepository.findById(issueDto.getUserAssignee()).get());
                issue.setType(issueDto.getType());
                issueRepository.save(issue);
                return issue;
            }
            else
            {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "You can't edit a issue you didn't create");
            }
        }
        else
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Couldn't find issue with the specified id");
        }
    }

    @PutMapping("/{id}/status")
    Issue editIssueStatus(@PathVariable Long id, @Valid @RequestBody String status,
        @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Date date = new Date();
            Issue issue = issueOpt.get();
            IssueStatus oldStatus = issue.getStatus();
            IssueStatus enumStatus = null;
            for (IssueStatus s : IssueStatus.values())
            {
                if (s.name().equals(status))
                {
                    enumStatus = s;
                }
            }
            if (enumStatus == null)
            {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "Specified status doesn't exist");
            }
            issue.setStatus(enumStatus);
            issue.setUpdateDate(date);
            List<Comment> comments = issue.getComments();
            Comment newComment = new Comment();
            newComment.setCreationDate(date);
            newComment.setText("Changed status from " + oldStatus.name() + " to " + status);
            newComment.setUserComment(userRepository.findByToken(token).get());
            comments.add(newComment);
            issue.setComments(comments);
            issueRepository.save(issue);
            return issue;
        }
        else
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Couldn't find issue with the specified id");
        }

    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete an issue")
    void deleteIssueById(@PathVariable Long id)
    {
        issueRepository.deleteById(id);
    }

    @PostMapping("/{id}/vote")
    @ApiOperation("Upvote an issue")
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
    @ApiOperation("Unvote an issue")
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
    @ApiOperation("Set an issue as watched")
    Issue watchIssue(@PathVariable Long id, @RequestHeader(value="api_key", defaultValue="-1") String api_key)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);

        if (!issueOpt.isPresent())
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Couldn't find issue with the specified id");
        }

        Optional<User> userOpt = userRepository.findByToken(api_key);

        if (!userOpt.isPresent())
        {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                "Action not available, unknown user");
        }

        User user = userOpt.get();
        Set<Issue> watchingIssues = user.getWatchingIssues();

        Issue issue = issueOpt.get();
        if (watchingIssues.contains(issue))
        {
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE,
                "Already watching this issue.");
        }

        watchingIssues.add(issue);

        user.setWatchingIssues(watchingIssues);
        userRepository.save(user);

        return issue;
    }

    @DeleteMapping("/{id}/watch")
    @ApiOperation("Unwatch an issue")
    Issue unwatchIssue(@PathVariable Long id, @RequestHeader(value="api_key", defaultValue="-1") String api_key)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);

        if (!issueOpt.isPresent())
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Couldn't find issue with the specified id");
        }

        Optional<User> userOpt = userRepository.findByToken(api_key);

        if (!userOpt.isPresent())
        {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                "Action not available, unknown user");
        }

        User user = userOpt.get();
        Set<Issue> watchingIssues = user.getWatchingIssues();

        Issue issue = issueOpt.get();
        if (!watchingIssues.contains(issue))
        {
            throw new HttpClientErrorException(HttpStatus.NOT_ACCEPTABLE,
                "Cannot unwatch this issue due to is not even watched");
        }

        watchingIssues.remove(issue);

        user.setWatchingIssues(watchingIssues);
        userRepository.save(user);

        return issue;
    }

    @GetMapping("/{id}/comments")
    List<Comment> getComments(@PathVariable Long id)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            return issueOpt.get().getComments();
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");

    }

    @PostMapping("/{id}/comments")
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
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }

    @PutMapping("/{id}/comments/{comm-id}")
    Comment editComment(@PathVariable Long id, @PathVariable(name = "comm-id") Long commId,
        @RequestBody CommentDto commentDto, @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            List<Comment> comments = issue.getComments();
            Comment commentOld = comments.stream().filter(comm -> comm.getId().equals(commId))
                .findFirst().orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST,
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
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "You can't edit a comment that it's not from your user");
            }
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }

    @DeleteMapping("/{id}/comments/{comm-id}")
    void deleteComment(@PathVariable Long id, @PathVariable(name = "comm-id") Long commId,
        @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            List<Comment> comments = issue.getComments();
            Comment commentOld = comments.stream().filter(comm -> comm.getId().equals(commId))
                .findFirst().orElseThrow(() -> new HttpClientErrorException(HttpStatus.BAD_REQUEST,
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
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "You can't delete a comment that it's not from your user");
            }
        }
        throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
            "Couldn't find issue with the specified id");
    }

    @PutMapping("{id}/attachments")
    Issue addAttachments(@PathVariable Long id, @RequestHeader("api_key") String token,
        @RequestParam("files") MultipartFile[] files) throws IOException
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            if (!issue.getUserCreator().equals(userRepository.findByToken(token).get()))
            {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                    "Can't add attachments to a issue you didn't create");
            }
            for (MultipartFile file : files)
            {
                UploadedFile newFile = new UploadedFile();
                newFile.setFile(file.getBytes());
                newFile.setName(file.getName());
                newFile.setContentType(file.getContentType());
                uploadedFileRepository.save(newFile);
                issue.addAttachment(newFile);
            }
            issueRepository.save(issue);
            return issue;
        }
        else
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Couldn't find a issue with the specified id");
        }
    }

    @GetMapping("{id}/attachments")
    Set<UploadedFile> getAttachments(@PathVariable Long id, @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            Issue issue = issueOpt.get();
            return issue.getAttachments();
        }
        else
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Couldn't find a issue with the specified id");
        }

    }

    @GetMapping("{id}/attachments/{fileId}")
    byte[] getSingleAttachment(@PathVariable Long id, @PathVariable Long fileId,
        @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            return uploadedFileRepository.findById(fileId).get().getFile();
        }
        else
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Couldn't find a issue with the specified id");
        }
    }

    @DeleteMapping("{id}/attachments/{fileId}")
    Set<UploadedFile> deleteAttachment(@PathVariable Long id, @PathVariable Long fileId,
        @RequestHeader("api_key") String token)
    {
        Optional<Issue> issueOpt = issueRepository.findById(id);
        if (issueOpt.isPresent())
        {
            uploadedFileRepository.delete(uploadedFileRepository.findById(fileId).get());
            issueOpt.get().getAttachments().removeIf(file -> file.getId().equals(fileId));
            return issueOpt.get().getAttachments();
        }
        else
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "Couldn't find a issue with the specified id");
        }

    }

}
