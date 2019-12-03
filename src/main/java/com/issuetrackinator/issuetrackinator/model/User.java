package com.issuetrackinator.issuetrackinator.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(position = 0)
    private Long id;

    @NotBlank
    @Column(unique = true)
    @ApiModelProperty(example = "tom", position = 1)
    private String username;

    @ApiModelProperty(example = "Tom Bombadil", position = 2)
    private String personalName;

    @NotBlank
    @Column(unique = true)
    @ApiModelProperty(example = "email@provider.com", position = 3)
    private String email;

    @NotBlank
    @ApiModelProperty(example = "password123", position = 4)
    private String password;

    @ApiModelProperty(example = "6afd6dd40f7ff2d557743da155c3629608391f819cd86c6c93d0596a033e45a6", position = 5)
    private String token;
    
    @JsonIgnore
    @ApiModelProperty(position = 6)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "watchers", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "issueId"))
    Set<Issue> watchingIssues;


    public User() { }

    public User(String username, String personalName, String email, String password) {
        this.username = username;
        this.personalName = personalName;
        this.email = email;
        this.password = password;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPersonalName()
    {
        return personalName;
    }

    public void setPersonalName(String personalName)
    {
        this.personalName = personalName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public Set<Issue> getWatchingIssues()
    {
        return watchingIssues;
    }

    public void setWatchingIssues(Set<Issue> watchingIssues)
    {
        this.watchingIssues = watchingIssues;
    }

}
