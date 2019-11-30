package com.issuetrackinator.issuetrackinator.model;

public class CommentDto
{

    private String text;

    private Long idUser;

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public Long getIdUser()
    {
        return idUser;
    }

    public void setIdUser(Long idUser)
    {
        this.idUser = idUser;
    }

}
