package com.issuetrackinator.issuetrackinator.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer")
public class Issue
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUserCreator")
    private User userCreator;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "idUserAsignee")
    private User userAssignee;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "votes", joinColumns = @JoinColumn(name = "issueId"), inverseJoinColumns = @JoinColumn(name = "userId"))
    Set<User> votesUsers;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private IssueStatus status;

    @NotNull
    private IssueType type;

    @NotNull
    private IssuePriority priority;

    private int votes;

    @NotNull
    private Date creationDate;

    @NotNull
    private Date updateDate;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public User getUserCreator()
    {
        return userCreator;
    }

    public void setUserCreator(User userCreator)
    {
        this.userCreator = userCreator;
    }

    public User getUserAsignee()
    {
        return userAssignee;
    }

    public void setUserAssignee(User userAssignee)
    {
        this.userAssignee = userAssignee;
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

    public int getVotes()
    {
        return votes;
    }

    public void setVotes(int votes)
    {
        this.votes = votes;
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

    public IssueStatus getStatus()
    {
        return status;
    }

    public void setStatus(IssueStatus status)
    {
        this.status = status;
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

    public Set<User> getVotesUsers()
    {
        return votesUsers;
    }

    public void setVotesUsers(Set<User> votesUsers)
    {
        this.votesUsers = votesUsers;
    }

}
