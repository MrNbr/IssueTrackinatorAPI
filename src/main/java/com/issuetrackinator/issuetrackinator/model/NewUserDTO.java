package com.issuetrackinator.issuetrackinator.model;

import io.swagger.annotations.ApiModelProperty;

public class NewUserDTO {

    @ApiModelProperty(example = "tom", position = 0)
    private String username;

    @ApiModelProperty(example = "Tom Bombadil", position = 1)
    private String personalName;

    @ApiModelProperty(example = "email@provider.com", position = 2)
    private String email;

    @ApiModelProperty(example = "password123", position = 3)
    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
