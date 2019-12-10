package com.issuetrackinator.issuetrackinator.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer")
public class Issue
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(position = 0)
    private Long id;

    @NotBlank
    @ApiModelProperty(example = "A new issue", position = 1)
    private String title;

    @ApiModelProperty(example = "This is the description of the new issue", position = 2)
    private String description;

    @NotNull
    @ApiModelProperty(example = "TASK", position = 3)
    private IssueType type;

    @NotNull
    @ApiModelProperty(example = "TRIVIAL", position = 4)
    private IssuePriority priority;

    @NotNull
    @ApiModelProperty(example = "NEW", position = 5)
    private IssueStatus status;

    @ApiModelProperty(position = 6)
    private int votes;

    @JsonIgnore
    @ApiModelProperty(position = 7)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "votes", joinColumns = @JoinColumn(name = "issueId"), inverseJoinColumns = @JoinColumn(name = "userId"))
    private Set<User> votesUsers;

    @ApiModelProperty(position = 8)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUserCreator")
    private User userCreator;

    @ApiModelProperty(position = 9)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "idUserAssignee")
    private User userAssignee;

    @ApiModelProperty(position = 10)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "comments", joinColumns = @JoinColumn(name = "issueId"), inverseJoinColumns = @JoinColumn(name = "commentId"))
    private List<Comment> comments;

    @JsonIgnore
    @ApiModelProperty(position = 11)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "attachments")
    private Set<UploadedFile> attachments;

    @NotNull
    @ApiModelProperty(position = 12)
    private Date creationDate;

    @NotNull
    @ApiModelProperty(position = 13)
    private Date updateDate;

    public Issue()
    {
    }

    public Issue(String title, String description, IssueType type, IssuePriority priority,
        User userCreator, User userAssignee)
    {
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = IssueStatus.NEW;
        this.votes = 0;
        this.userCreator = userCreator;
        this.userAssignee = userAssignee;
        Date date = new Date();
        this.creationDate = date;
        this.updateDate = date;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public IssueType getType()
    {
        return type;
    }

    public void setType(IssueType type)
    {
        this.type = type;
    }

    public IssuePriority getPriority()
    {
        return priority;
    }

    public void setPriority(IssuePriority priority)
    {
        this.priority = priority;
    }

    public IssueStatus getStatus()
    {
        return status;
    }

    public void setStatus(IssueStatus status)
    {
        this.status = status;
    }

    public int getVotes()
    {
        return votes;
    }

    public void setVotes(int votes)
    {
        this.votes = votes;
    }

    public Set<User> getVotesUsers()
    {
        return votesUsers;
    }

    public void setVotesUsers(Set<User> votesUsers)
    {
        this.votesUsers = votesUsers;
    }

    public User getUserCreator()
    {
        return userCreator;
    }

    public void setUserCreator(User userCreator)
    {
        this.userCreator = userCreator;
    }

    public User getUserAssignee()
    {
        return userAssignee;
    }

    public void setUserAssignee(User userAssignee)
    {
        this.userAssignee = userAssignee;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public List<Comment> getComments()
    {
        return comments;
    }

    public void setComments(List<Comment> comments)
    {
        this.comments = comments;
    }

    public Set<UploadedFile> getAttachments()
    {
        return attachments;
    }

    public void setAttachments(Set<UploadedFile> attachments)
    {
        this.attachments = attachments;
    }

    public void addAttachment(UploadedFile file)
    {
        this.attachments.add(file);
    }

}
