package com.issuetrackinator.issuetrackinator.model;

import java.util.Set;
import javax.persistence.FetchType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties("hibernateLazyInitializer")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    private String personalName;

    @NotBlank
    @Column(unique = true)
    private String email;
    
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "watchers", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "issueId"))
    Set<Issue> watchingIssues;

    @NotBlank
    private String password;

    private String token;

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
