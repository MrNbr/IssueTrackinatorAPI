package com.issuetrackinator.issuetrackinator.model;

//I'll suppose this class will be only used for creating a new Issue
public class IssueDto
{
    private Long userCreator;

    private Long userAssignee;

    private String title;

    private String description;

    private IssueType type;

    private IssuePriority priority;

    public Long getUserCreator()
    {
        return userCreator;
    }

    public void setUserCreator(Long userCreator)
    {
        this.userCreator = userCreator;
    }

    public Long getUserAssignee()
    {
        return userAssignee;
    }

    public void setUserAssignee(Long userAssignee)
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

}
