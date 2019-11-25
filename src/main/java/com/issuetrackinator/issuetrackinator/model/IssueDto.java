package com.issuetrackinator.issuetrackinator.model;

//I'll suppose this class will be only used for creating a new Issue
public class IssueDto
{
    private Long userCreator;

    private Long userAssignee;

    private String title;

    private String description;

    private String status;

    private String type;

    private String priority;

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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

}
