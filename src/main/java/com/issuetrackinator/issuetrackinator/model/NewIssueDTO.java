package com.issuetrackinator.issuetrackinator.model;

import io.swagger.annotations.ApiModelProperty;

public class NewIssueDTO
{

    @ApiModelProperty(example = "A new issue", position = 0)
    private String title;

    @ApiModelProperty(example = "This is the description of the new issue", position = 1)
    private String description;

    @ApiModelProperty(example = "TASK", position = 2)
    private IssueType type;

    @ApiModelProperty(example = "TRIVIAL", position = 3)
    private IssuePriority priority;

    @ApiModelProperty(position = 4)
    private Long userAssigneeId;

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

    public Long getUserAssigneeId()
    {
        return userAssigneeId;
    }

    public void setUserAssigneeId(Long userAssigneeId)
    {
        this.userAssigneeId = userAssigneeId;
    }

}
