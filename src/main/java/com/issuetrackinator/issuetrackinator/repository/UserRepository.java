package com.issuetrackinator.issuetrackinator.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.issuetrackinator.issuetrackinator.model.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByToken(String token);

}
