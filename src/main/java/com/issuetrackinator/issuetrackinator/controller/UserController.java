package com.issuetrackinator.issuetrackinator.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.hash.Hashing;
import com.issuetrackinator.issuetrackinator.model.NewUserDTO;
import com.issuetrackinator.issuetrackinator.model.User;
import com.issuetrackinator.issuetrackinator.model.UserCredentialsDTO;
import com.issuetrackinator.issuetrackinator.repository.UserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "User controller")
@RestController
@CrossOrigin
@RequestMapping(path = "/api" + UserController.USER_PATH)
@JsonIgnoreProperties("hibernateLazyInitializer")
public class UserController
{
    @Autowired
    private UserRepository userRepository;

    static final String USER_PATH = "/users";

    private static final String emailRegex = "^(.+)@(.+)$";

    @GetMapping
    @ApiOperation("Get all the users")
    List<User> getUsers(@RequestHeader("api_key") String token)
    {
        List<User> users = userRepository.findAll();
        User userRequest = userRepository.findByToken(token).get();
        for (User user : users)
        {
            if (!user.equals(userRequest))
            {
                user.setToken(null);
            }
        }
        return users;
    }

    @GetMapping("{id}")
    @ApiOperation("Get a user by the id")
    User getUserById(@PathVariable final Long id, @RequestHeader("api_key") String token)
    {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent())
        {
            if (userRepository.findByToken(token).get().equals(user.get()))
            {
                return user.get();
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can't get user info of another user that it's not yours");
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Couldn't find a user with the specified id");
    }

    @PostMapping
    @ApiOperation("Create a new user")
    @ResponseStatus(HttpStatus.CREATED)
    User createUser(@Valid @RequestBody NewUserDTO userDTO)
    {
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(userDTO.getEmail());
        if (!matcher.matches())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The email is not valid");
        }
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "The username is already taken");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
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

    @PutMapping("/{id}")
    User editUser(@RequestHeader("api_key") String token, @PathVariable final Long id,
        @RequestBody User newUser)
    {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user doesn't exist");
        }
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(newUser.getEmail());
        if (!matcher.matches())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The email is not valid");
        }
        if (!newUser.getUsername().equals(userOpt.get().getUsername())
            && userRepository.findByUsername(newUser.getUsername()).isPresent())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "The username is already taken");
        }
        if (!newUser.getEmail().equalsIgnoreCase(userOpt.get().getEmail())
            && userRepository.findByEmail(newUser.getEmail()).isPresent())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "The email is already in use");
        }
        User user = userOpt.get();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPersonalName(newUser.getPersonalName());
        user.setPassword(
            Hashing.sha256().hashString(newUser.getPassword(), StandardCharsets.UTF_8).toString());
        user.setToken(
            Hashing.sha256().hashString(user.getUsername() + user.getEmail() + user.getPassword(),
                StandardCharsets.UTF_8).toString());
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete a user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUserById(@PathVariable final Long id, @RequestHeader("api_key") String token)
    {
        if (userRepository.findByToken(token).get().equals(userRepository.findById(id).get()))
        {
            userRepository.deleteById(id);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Can't delete a user that it's not yours");
        }

    }

    @GetMapping("/regenerateToken")
    String regenerateToken(@RequestBody UserCredentialsDTO credentials)
    {
        String credKey;
        if (credentials.getUsername() != null)
        {
            credKey = credentials.getUsername();
            if (credentials.getPassword() == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is necessary");
            }
            Optional<User> userOpt = userRepository.findByUsername(credKey);
            if (userOpt.isPresent() && userOpt.get().getPassword().equals(Hashing.sha256()
                .hashString(credentials.getPassword(), StandardCharsets.UTF_8).toString()))
            {
                return userOpt.get().getToken();
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Credentials are not correct");
            }
        }
        else if (credentials.getEmail() != null)
        {
            credKey = credentials.getEmail();
            if (credentials.getPassword() == null)
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is necessary");
            }
            Optional<User> userOpt = userRepository.findByEmail(credKey);
            if (userOpt.isPresent() && userOpt.get().getPassword().equals(Hashing.sha256()
                .hashString(credentials.getPassword(), StandardCharsets.UTF_8).toString()))
            {
                return userOpt.get().getToken();
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Credentials are not correct");
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Email or username is necessary");
        }

    }

}
