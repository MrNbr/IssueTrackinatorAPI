package com.issuetrackinator.issuetrackinator.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.hash.Hashing;
import com.issuetrackinator.issuetrackinator.model.NewUserDTO;
import com.issuetrackinator.issuetrackinator.model.User;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping(path = "/api" + UserController.USER_PATH)
@JsonIgnoreProperties("hibernateLazyInitializer")
public class UserController
{
    @Autowired
    private UserRepository userRepository;

    static final String USER_PATH = "/users";

    private static final String emailRegex = "^(.+)@(.+)$";

    @GetMapping
    @ApiOperation(value = "Get all the users", tags = "User controller")
    List<User> getUsers()
    {
        return userRepository.findAll();
    }

    @GetMapping("{id}")
    @ApiOperation(value = "Get a user by the id", tags = "User controller")
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
    @ApiOperation(value = "Create a new user", tags = "User controller")
    User createUser(@Valid @RequestBody NewUserDTO userDTO)
    {

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(userDTO.getEmail());
        if (!matcher.matches())
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "The email is not valid");
        }
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent())
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "The username is already taken");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
        {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,
                "The email is already in use");
        }
        // Creating an instance of User with the content of the DTO
        User user = new User(userDTO.getUsername(), userDTO.getPersonalName(), userDTO.getEmail(),
                             userDTO.getPassword());
        user.setPassword(
            Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString());
        user.setToken(
            Hashing.sha256().hashString(user.getUsername() + user.getEmail() + user.getPassword(),
                StandardCharsets.UTF_8).toString());
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a user", tags = "User controller")
    void deleteUserById(@PathVariable final Long id)
    {
        userRepository.deleteById(id);
    }

}
