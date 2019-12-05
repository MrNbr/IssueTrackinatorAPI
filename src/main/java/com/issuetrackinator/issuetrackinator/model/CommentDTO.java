package com.issuetrackinator.issuetrackinator.model;

import io.swagger.annotations.ApiModelProperty;

public class CommentDTO
{

    @ApiModelProperty(position = 0)
    private Long idUser;

    @ApiModelProperty(example = "This issue is alright!", position = 1)
    private String text;


    public Long getIdUser()
    {
        return idUser;
    }

    public void setIdUser(Long idUser)
    {
        this.idUser = idUser;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

}
