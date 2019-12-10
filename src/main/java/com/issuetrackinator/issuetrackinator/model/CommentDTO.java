package com.issuetrackinator.issuetrackinator.model;

import io.swagger.annotations.ApiModelProperty;

public class CommentDTO
{
    @ApiModelProperty(example = "This issue is alright!", position = 1)
    private String text;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

}
