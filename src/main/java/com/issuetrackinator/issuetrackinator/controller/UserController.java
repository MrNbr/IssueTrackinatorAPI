package com.issuetrackinator.issuetrackinator.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.hash.Hashing;
import com.issuetrackinator.issuetrackinator.model.User;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;

@RestController
@RequestMapping(path = "/api" + UserController.USER_PATH)
@JsonIgnoreProperties("hibernateLazyInitializer")
public class UserController
{
    @Autowired
    private UserRepository userRepository;

    static final String USER_PATH = "/users";

    private String emailRegex = "^(.+)@(.+)$";

    @GetMapping
    List<User> getUsers()
    {
        return userRepository.findAll();
    }

    @GetMapping("{id}")
    User getUserById(@PathVariable final Long id)
    {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent())
        {
            return user.get();
        }
        throw new HttpClientErrorException(HttpStatus.FORBIDDEN,
            "Couldn't find a user with the specified id");
    }

    @PostMapping
    User createUser(@Valid @RequestBody User user)
    {

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches())
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "The email is not valid");
        }
        if (userRepository.findByUsername(user.getUsername()).isPresent())
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "The username is already taken");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent())
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "The email is already in use");
        }
        user.setPassword(
            Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString());
        user.setToken(
            Hashing.sha256().hashString(user.getUsername() + user.getEmail() + user.getPassword(),
                StandardCharsets.UTF_8).toString());
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    void deleteUserById(@PathVariable final Long id)
    {
        userRepository.deleteById(id);
    }

}
